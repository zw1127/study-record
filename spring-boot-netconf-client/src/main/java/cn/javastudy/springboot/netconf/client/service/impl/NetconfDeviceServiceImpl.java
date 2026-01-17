package cn.javastudy.springboot.netconf.client.service.impl;

import cn.javastudy.springboot.netconf.client.entity.NetconfDeviceInfo;
import cn.javastudy.springboot.netconf.client.entity.RemoteDeviceId;
import cn.javastudy.springboot.netconf.client.listener.NetconfDeviceListener;
import cn.javastudy.springboot.netconf.client.service.NetconfDeviceService;
import cn.javastudy.springboot.netconf.client.service.NetconfServices;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashMap;
import org.opendaylight.netconf.client.NetconfClientDispatcher;
import org.opendaylight.netconf.client.NetconfClientSessionListener;
import org.opendaylight.netconf.client.conf.NetconfClientConfiguration;
import org.opendaylight.netconf.client.conf.NetconfReconnectingClientConfiguration;
import org.opendaylight.netconf.client.conf.NetconfReconnectingClientConfigurationBuilder;
import org.opendaylight.netconf.nettyutil.ReconnectStrategyFactory;
import org.opendaylight.netconf.nettyutil.TimedReconnectStrategyFactory;
import org.opendaylight.netconf.nettyutil.handler.ssh.authentication.LoginPasswordHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfDeviceServiceImpl implements NetconfDeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfServicesImpl.class);

    private static final int DEFAULT_MAX_CONNECTION_ATTEMPTS = 0;
    private static final int DEFAULT_BETWEEN_ATTEMPTS_TIMEOUT_MILLIS = 2000;
    private static final long DEFAULT_CONNECTION_TIMEOUT_MILLIS = 20000L;
    private static final BigDecimal DEFAULT_SLEEP_FACTOR = new BigDecimal(1.5);

    private final HashMap<String, NetconfDeviceListener> activeConnectors = new HashMap<>();

    private final NetconfServices netconfServices;
    private final NetconfClientDispatcher dispatcher;

    public NetconfDeviceServiceImpl(NetconfServices netconfServices) {
        this.netconfServices = netconfServices;
        this.dispatcher = new NetconfClientDispatcherDelegateImpl(netconfServices);
    }

    @Override
    public ListenableFuture<RemoteDeviceId> connectDevice(NetconfDeviceInfo deviceInfo) {
        InetSocketAddress address = getInetSocketAddress(deviceInfo);
        String nodeId = deviceInfo.getDeviceId();
        RemoteDeviceId deviceId = new RemoteDeviceId(nodeId, address);
        NetconfDeviceListener listener = new NetconfDeviceListener(deviceId, netconfServices.getNotificationService());
        NetconfReconnectingClientConfiguration config = getClientConfig(listener, deviceInfo);

        ListenableFuture<RemoteDeviceId> future = listener.initializeRemoteConnection(dispatcher, config);

        activeConnectors.put(nodeId, listener);

        Futures.addCallback(future, new FutureCallback<RemoteDeviceId>() {
            @Override
            public void onSuccess(final RemoteDeviceId result) {
                LOG.debug("Connector for {} started succesfully", deviceId);
            }

            @Override
            public void onFailure(final Throwable throwable) {
                LOG.error("Connector for {} failed", deviceId, throwable);
                // remove this node from active connectors?
            }
        }, MoreExecutors.directExecutor());

        return future;
    }

    public NetconfReconnectingClientConfiguration getClientConfig(final NetconfClientSessionListener listener,
                                                                  final NetconfDeviceInfo node) {

        //setup default values since default value is not supported in mdsal
        final long clientConnectionTimeoutMillis = node.getConnectionTimeoutMillis() == null
            ? DEFAULT_CONNECTION_TIMEOUT_MILLIS : node.getConnectionTimeoutMillis();
        final long maxConnectionAttempts = node.getMaxConnectionAttempts() == null
            ? DEFAULT_MAX_CONNECTION_ATTEMPTS : node.getMaxConnectionAttempts();
        final int betweenAttemptsTimeoutMillis = node.getBetweenAttemptsTimeoutMillis() == null
            ? DEFAULT_BETWEEN_ATTEMPTS_TIMEOUT_MILLIS : node.getBetweenAttemptsTimeoutMillis();
        final BigDecimal sleepFactor = node.getSleepFactor() == null ? DEFAULT_SLEEP_FACTOR : node.getSleepFactor();

        final InetSocketAddress socketAddress = new InetSocketAddress(node.getAddress(), node.getPort());

        final ReconnectStrategyFactory sf = new TimedReconnectStrategyFactory(netconfServices.getEventExecutor(),
            maxConnectionAttempts, betweenAttemptsTimeoutMillis, sleepFactor);

        LoginPasswordHandler loginPasswordHandler = new LoginPasswordHandler(node.getUsername(), node.getPassword());
        return NetconfReconnectingClientConfigurationBuilder.create()
            .withProtocol(NetconfClientConfiguration.NetconfClientProtocol.SSH)
            .withAuthHandler(loginPasswordHandler)
            .withAddress(socketAddress)
            .withConnectionTimeoutMillis(clientConnectionTimeoutMillis)
            .withReconnectStrategy(sf.createReconnectStrategy())
            .withConnectStrategyFactory(sf)
            .withSessionListener(listener)
            .build();
    }

    private InetSocketAddress getInetSocketAddress(NetconfDeviceInfo deviceInfo) {
        return new InetSocketAddress(deviceInfo.getAddress(), deviceInfo.getPort());
    }

    @Override
    public ListenableFuture<Void> disconnectDevice(String deviceId) {
        LOG.debug("Disconnecting RemoteDevice{{}}", deviceId);

        final NetconfDeviceListener deviceListener = activeConnectors.remove(deviceId);
        if (deviceListener == null) {
            return Futures.immediateFailedFuture(
                new IllegalStateException("Unable to disconnect device that is not connected"));
        }

        deviceListener.close();
        return Futures.immediateFuture(null);
    }
}
