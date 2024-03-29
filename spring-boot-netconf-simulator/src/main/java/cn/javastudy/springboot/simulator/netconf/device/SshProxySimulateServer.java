package cn.javastudy.springboot.simulator.netconf.device;

import static java.util.Objects.requireNonNull;

import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import com.google.common.collect.ImmutableList;
import io.netty.channel.EventLoopGroup;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.opendaylight.netconf.shaded.sshd.common.FactoryManager;
import org.opendaylight.netconf.shaded.sshd.common.NamedFactory;
import org.opendaylight.netconf.shaded.sshd.common.cipher.BuiltinCiphers;
import org.opendaylight.netconf.shaded.sshd.common.cipher.Cipher;
import org.opendaylight.netconf.shaded.sshd.common.io.IoAcceptor;
import org.opendaylight.netconf.shaded.sshd.common.io.IoConnectFuture;
import org.opendaylight.netconf.shaded.sshd.common.io.IoConnector;
import org.opendaylight.netconf.shaded.sshd.common.io.IoHandler;
import org.opendaylight.netconf.shaded.sshd.common.io.IoServiceEventListener;
import org.opendaylight.netconf.shaded.sshd.common.io.IoServiceFactory;
import org.opendaylight.netconf.shaded.sshd.common.io.IoServiceFactoryFactory;
import org.opendaylight.netconf.shaded.sshd.common.io.IoSession;
import org.opendaylight.netconf.shaded.sshd.common.io.nio2.Nio2Acceptor;
import org.opendaylight.netconf.shaded.sshd.common.io.nio2.Nio2Connector;
import org.opendaylight.netconf.shaded.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.opendaylight.netconf.shaded.sshd.common.session.Session;
import org.opendaylight.netconf.shaded.sshd.common.session.SessionHeartbeatController.HeartbeatType;
import org.opendaylight.netconf.shaded.sshd.common.session.SessionListener;
import org.opendaylight.netconf.shaded.sshd.common.util.closeable.AbstractCloseable;
import org.opendaylight.netconf.shaded.sshd.core.CoreModuleProperties;
import org.opendaylight.netconf.shaded.sshd.server.SshServer;
import org.opendaylight.netconf.ssh.RemoteNetconfCommand;
import org.opendaylight.netconf.ssh.SshProxyServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 增强 odl netconf项目的SshProxyServer，支持由服务端发起连接.
 */
public class SshProxySimulateServer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SshProxySimulateServer.class);

    private final Map<InetSocketAddress, IoConnectFuture> callhomeServerMap = new ConcurrentHashMap<>();
    private final Map<InetSocketAddress, Session> sessionMap = new ConcurrentHashMap<>();

    private final SshServer sshServer;
    private final ScheduledExecutorService minaTimerExecutor;
    private final EventLoopGroup clientGroup;
    private final IoServiceFactoryFactory nioServiceWithPoolFactoryFactory;
    private final String deviceId;

    private IoConnector connector;

    private SshProxySimulateServer(final ScheduledExecutorService minaTimerExecutor, final EventLoopGroup clientGroup,
                                   final IoServiceFactoryFactory serviceFactory, final String deviceId) {
        this.minaTimerExecutor = minaTimerExecutor;
        this.clientGroup = clientGroup;
        nioServiceWithPoolFactoryFactory = serviceFactory;
        this.deviceId = deviceId;
        sshServer = SshServer.setUpDefaultServer();
    }

    /**
     * Create a server with a shared {@link AsynchronousChannelGroup}. Unlike the other constructor, this does
     * not create a dedicated thread group, which is useful when you need to start a large number of servers and do
     * not want to have a thread group (and hence an anonyous thread) for each of them.
     */
    public SshProxySimulateServer(final ScheduledExecutorService minaTimerExecutor, final EventLoopGroup clientGroup,
                                  final AsynchronousChannelGroup group, final String deviceId) {
        this(minaTimerExecutor, clientGroup, new SharedNioServiceFactoryFactory(group), deviceId);
    }

    public void bind(final SshProxyServerConfiguration sshProxyServerConfiguration) throws IOException {
        sshServer.setHost(sshProxyServerConfiguration.getBindingAddress().getHostString());
        sshServer.setPort(sshProxyServerConfiguration.getBindingAddress().getPort());

        //remove rc4 ciphers
        final List<NamedFactory<Cipher>> cipherFactories = sshServer.getCipherFactories();
        cipherFactories.removeIf(factory -> factory.getName().contains(BuiltinCiphers.arcfour128.getName())
            || factory.getName().contains(BuiltinCiphers.arcfour256.getName()));
        sshServer.setPasswordAuthenticator(
            (username, password, session)
                -> sshProxyServerConfiguration.getAuthenticator().authenticated(username, password));

        sshProxyServerConfiguration.getPublickeyAuthenticator().ifPresent(sshServer::setPublickeyAuthenticator);

        sshServer.setKeyPairProvider(sshProxyServerConfiguration.getKeyPairProvider());

        sshServer.setIoServiceFactoryFactory(nioServiceWithPoolFactoryFactory);
        sshServer.setScheduledExecutorService(minaTimerExecutor);

        final int idleTimeoutMillis = sshProxyServerConfiguration.getIdleTimeout();
        final Duration idleTimeout = Duration.ofMillis(idleTimeoutMillis);
        CoreModuleProperties.IDLE_TIMEOUT.set(sshServer, idleTimeout);

        final Duration nioReadTimeout;
        if (idleTimeoutMillis > 0) {
            final long heartBeat = idleTimeoutMillis * 333333L;
            sshServer.setSessionHeartbeat(HeartbeatType.IGNORE, TimeUnit.NANOSECONDS, heartBeat);
            nioReadTimeout = Duration.ofMillis(idleTimeoutMillis + TimeUnit.SECONDS.toMillis(15L));
        } else {
            nioReadTimeout = Duration.ZERO;
        }
        CoreModuleProperties.NIO2_READ_TIMEOUT.set(sshServer, nioReadTimeout);
        CoreModuleProperties.AUTH_TIMEOUT.set(sshServer, idleTimeout);
        CoreModuleProperties.TCP_NODELAY.set(sshServer, Boolean.TRUE);
        CoreModuleProperties.REKEY_TIME_LIMIT.set(sshServer, Duration.ofSeconds(-1));

        final RemoteNetconfCommand.NetconfCommandFactory netconfCommandFactory =
            new RemoteNetconfCommand.NetconfCommandFactory(clientGroup,
                sshProxyServerConfiguration.getLocalAddress());
        sshServer.setSubsystemFactories(ImmutableList.of(netconfCommandFactory));
        sshServer.start();

        sshServer.addSessionListener(createSessionListener());
        this.connector = sshServer.getIoServiceFactory().createConnector(sshServer.getSessionFactory());
    }

    public IoConnectFuture connect(final InetSocketAddress address) {
        IoConnectFuture connectFuture = reconnect(address);

        callhomeServerMap.put(address, connectFuture);
        return connectFuture;
    }

    public void disconnect(final InetSocketAddress address) {
        IoConnectFuture connectFuture = callhomeServerMap.remove(address);
        if (connectFuture != null) {
            IoSession session = connectFuture.getSession();
            if (session != null) {
                session.close(true);
            }
        }
    }

    public Map<InetSocketAddress, Session> getSessions() {
        return Map.copyOf(sessionMap);
    }

    private IoConnectFuture reconnect(final InetSocketAddress address) {
        return requireNonNull(connector).connect(address, null, null);
    }

    SessionListener createSessionListener() {
        return new SessionListener() {

            private final AtomicReference<ScheduledFuture<?>> scheduledFutrueRefence = new AtomicReference<>();

            private final AtomicBoolean isConnected = new AtomicBoolean(false);

            @Override
            public void sessionEvent(final Session session, final Event event) {
                LOG.info("device:{} SSH session {} event {}", deviceId, session, event);
            }

            @Override
            public void sessionCreated(final Session session) {
                LOG.info("SSH session {} created", session);
                isConnected.set(true);
                scheduledFutrueRefence.getAndUpdate(prev -> {
                    if (prev != null) {
                        boolean cancelled = prev.cancel(true);
                        LOG.info("device:{} remote:{} is connected, cancel scheduled task, result:{}",
                            deviceId, session.getRemoteAddress(), cancelled);
                    }

                    return null;
                });
                SocketAddress remoteAddress = session.getRemoteAddress();
                if (remoteAddress instanceof InetSocketAddress) {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;

                    sessionMap.put(inetSocketAddress, session);
                }
            }

            @Override
            public void sessionClosed(final Session session) {
                LOG.info("device:{} SSH Session {} closed", deviceId, session);
                isConnected.set(false);
                SocketAddress remoteAddress = session.getRemoteAddress();
                if (remoteAddress instanceof InetSocketAddress) {
                    InetSocketAddress callhomeServerAddress = (InetSocketAddress) remoteAddress;
                    sessionMap.remove(callhomeServerAddress);
                    if (callhomeServerMap.containsKey(callhomeServerAddress)) {
                        LOG.info("start reconnect task, target:{}.", callhomeServerAddress);
                        scheduledFutrueRefence.getAndUpdate(prev -> {
                            if (prev != null) {
                                prev.cancel(true);
                            }

                            return minaTimerExecutor.scheduleWithFixedDelay(() -> {
                                if (isConnected.get()) {
                                    LOG.info("device:{} target:{} is connected.", deviceId, remoteAddress);
                                    return;
                                }

                                LOG.info("sleep 10 seconds, device:{} reconnect target:{}.",
                                    deviceId, callhomeServerAddress);
                                reconnect(callhomeServerAddress);
                            }, 10, 10, TimeUnit.SECONDS);
                        });
                    }
                }
            }

        };
    }


    @Override
    public void close() throws IOException {
        if (connector != null) {
            connector.close();
        }

        try {
            sshServer.stop(true);
        } finally {
            sshServer.close(true);
        }
    }

    private abstract static class AbstractNioServiceFactory extends AbstractCloseable implements IoServiceFactory {
        private final FactoryManager manager;
        private final AsynchronousChannelGroup group;
        private final ExecutorService resumeTasks;
        private IoServiceEventListener eventListener;

        AbstractNioServiceFactory(final FactoryManager manager, final AsynchronousChannelGroup group,
                                  final ExecutorService resumeTasks) {
            this.manager = requireNonNull(manager);
            this.group = requireNonNull(group);
            this.resumeTasks = requireNonNull(resumeTasks);
        }

        @Override
        public final IoConnector createConnector(final IoHandler handler) {
            return new Nio2Connector(manager, handler, group, resumeTasks);
        }

        @Override
        public final IoAcceptor createAcceptor(final IoHandler handler) {
            return new Nio2Acceptor(manager, handler, group, resumeTasks);
        }

        @Override
        public final IoServiceEventListener getIoServiceEventListener() {
            return eventListener;
        }

        @Override
        public final void setIoServiceEventListener(final IoServiceEventListener listener) {
            eventListener = listener;
        }
    }

    private static final class SharedNioServiceFactory extends AbstractNioServiceFactory {
        SharedNioServiceFactory(final FactoryManager manager, final AsynchronousChannelGroup group,
                                final ExecutorService resumeTasks) {
            super(manager, group, resumeTasks);
        }
    }

    private static final class SharedNioServiceFactoryFactory extends Nio2ServiceFactoryFactory {
        private final AsynchronousChannelGroup group;

        SharedNioServiceFactoryFactory(final AsynchronousChannelGroup group) {
            this.group = requireNonNull(group);
        }

        @Override
        public IoServiceFactory create(final FactoryManager manager) {
            return new SharedNioServiceFactory(manager, group, Executors.newSingleThreadExecutor());
        }
    }
}
