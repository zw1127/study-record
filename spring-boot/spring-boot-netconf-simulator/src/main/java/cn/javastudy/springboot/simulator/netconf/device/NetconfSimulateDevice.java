package cn.javastudy.springboot.simulator.netconf.device;

import static cn.javastudy.springboot.simulator.netconf.utils.Utils.DEFAULT_AUTH_PROVIDER;
import static cn.javastudy.springboot.simulator.netconf.utils.Utils.getInetAddress;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.nio.NioEventLoopGroup;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;
import org.opendaylight.netconf.api.NetconfServerDispatcher;
import org.opendaylight.netconf.shaded.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.opendaylight.netconf.ssh.SshProxyServerConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfSimulateDevice {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfSimulateDevice.class);

    private final ScheduledExecutorService minaTimerExecutor;
    private SshProxySimulateServer simulateServer;
    private ChannelFuture localServer;

    private final NetconfServerDispatcher netconfServerDispatcher;
    private final NioEventLoopGroup nettyThreadgroup;
    private final AsynchronousChannelGroup group;
    private final String portNumber;
    private final String uniqueKey;

    private final SimulateDeviceInfo deviceInfo;
    private final DeviceSessionManager sessionManager;

    public NetconfSimulateDevice(final NetconfServerDispatcher netconfServerDispatcher,
                                 final NioEventLoopGroup nettyThreadgroup,
                                 final AsynchronousChannelGroup group,
                                 final SimulateDeviceInfo deviceInfo,
                                 final DeviceSessionManager sessionManager) {
        this.netconfServerDispatcher = netconfServerDispatcher;
        this.nettyThreadgroup = nettyThreadgroup;
        this.group = group;
        this.deviceInfo = deviceInfo;
        this.portNumber = requireNonNull(deviceInfo.getPortNumber()).toString();
        this.uniqueKey = requireNonNull(deviceInfo.getUniqueKey());
        this.sessionManager = sessionManager;

        this.minaTimerExecutor = newScheduledThreadPool(1, new ThreadFactoryBuilder()
            .setNameFormat("netconf-ssh-simulator-timer-" + uniqueKey + "-%d")
            .build());

    }

    @SuppressWarnings("IllegalCatch")
    public ListenableFuture<Boolean> start() {
        SettableFuture<Boolean> settableFuture = SettableFuture.create();
        try {
            LocalAddress localAddress = new LocalAddress(portNumber);
            this.localServer = netconfServerDispatcher.createLocalServer(localAddress);

            simulateServer = new SshProxySimulateServer(minaTimerExecutor, nettyThreadgroup, group);

            InetSocketAddress inetAddress = getInetAddress("0.0.0.0", portNumber);
            SshProxyServerConfigurationBuilder builder = new SshProxyServerConfigurationBuilder();

            builder.setBindingAddress(inetAddress);
            builder.setLocalAddress(localAddress);
            builder.setAuthenticator(DEFAULT_AUTH_PROVIDER);
            builder.setIdleTimeout(Integer.MAX_VALUE);
            builder.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

            localServer.addListener(future -> {
                if (future.isDone() && !future.isCancellable()) {
                    try {
                        settableFuture.set(Boolean.TRUE);
                        simulateServer.bind(builder.createSshProxyServerConfiguration());
                        LOG.info("Netconf Simulator:{} SSH endpoint started succssful at:{}", inetAddress, uniqueKey);
                    } catch (final IOException e) {
                        LOG.warn("Unable to start SSH netconf simulate server.");
                        settableFuture.setException(e);
                    }
                } else {
                    LOG.warn("Unalbe to start SSH netconf server:{} at:{}", uniqueKey, inetAddress, future.cause());
                    settableFuture.set(Boolean.FALSE);
                }
            });
        } catch (Throwable throwable) {
            LOG.warn("start device:{} error.", this, throwable);
            settableFuture.setException(throwable);
        }

        return settableFuture;
    }

    @SuppressWarnings("IllegalCatch")
    public ListenableFuture<Boolean> callhomeConnect(InetSocketAddress address) {
        if (simulateServer == null) {
            LOG.warn("Simulate server does not started.");
            return Futures.immediateFailedFuture(new RuntimeException("Simulate server does not started."));
        }

        try {
            SettableFuture<Boolean> settableFuture = SettableFuture.create();
            simulateServer.connect(address)
                .addListener(future -> {
                    if (future.isConnected()) {
                        settableFuture.set(Boolean.TRUE);
                    } else {
                        settableFuture.set(Boolean.FALSE);
                    }

                    if (future.getException() != null) {
                        settableFuture.setException(future.getException());
                    }
                });

            return settableFuture;
        } catch (Throwable throwable) {
            return Futures.immediateFailedFuture(throwable);
        }
    }

    public ListenableFuture<Boolean> callhomeConnect(String ipaddress, Integer port) {
        InetSocketAddress inetAddress = getInetAddress(ipaddress, port.toString());
        return callhomeConnect(inetAddress);
    }

    public void callhomeDisconnect(String ipaddress, Integer port) {
        InetSocketAddress inetAddress = getInetAddress(ipaddress, port.toString());
        callhomeDisconnect(inetAddress);
    }

    public void callhomeDisconnect(InetSocketAddress address) {
        simulateServer.disconnect(address);
    }

    @SuppressWarnings("IllegalCatch")
    public void stop() {
        LOG.info("EventExecutro is being removed, closing netconf ssh server.");
        try {
            if (localServer != null) {
                if (simulateServer != null) {
                    simulateServer.close();
                }

                if (localServer.isDone()) {
                    localServer.channel().close();
                } else {
                    localServer.cancel(true);
                }
            }

            if (minaTimerExecutor != null) {
                minaTimerExecutor.shutdownNow();
            }
        } catch (Throwable e) {
            LOG.error("Closing of ssh server failed", e);
        }
    }

    public SimulateDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public DeviceSessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NetconfSimulateDevice.class.getSimpleName() + "[", "]")
            .add("deviceId='" + uniqueKey + "'")
            .add("port='" + portNumber + "'")
            .toString();
    }
}
