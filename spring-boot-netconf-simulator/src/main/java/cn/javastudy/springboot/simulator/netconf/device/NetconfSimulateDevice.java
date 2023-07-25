package cn.javastudy.springboot.simulator.netconf.device;

import static cn.javastudy.springboot.simulator.netconf.utils.Utils.DEFAULT_AUTH_PROVIDER;
import static cn.javastudy.springboot.simulator.netconf.utils.Utils.getInetAddress;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import cn.javastudy.springboot.simulator.netconf.domain.CallhomeInfo;
import cn.javastudy.springboot.simulator.netconf.domain.Result;
import cn.javastudy.springboot.simulator.netconf.domain.ResultBuilder;
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
    public ListenableFuture<Result<SimulateDeviceInfo>> start() {
        SettableFuture<Result<SimulateDeviceInfo>> settableFuture = SettableFuture.create();
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
                        simulateServer.bind(builder.createSshProxyServerConfiguration());
                        settableFuture.set(ResultBuilder.success(deviceInfo).build());
                        LOG.info("Netconf Simulator:{} SSH endpoint started succssful at:{}", inetAddress, uniqueKey);
                    } catch (final IOException e) {
                        LOG.warn("Unable to start SSH netconf simulate server.", e);
                        settableFuture.set(ResultBuilder.failed(deviceInfo, "bind error").build());
                    }
                } else {
                    LOG.warn("Unalbe to start SSH netconf server:{} at:{}", uniqueKey, inetAddress, future.cause());
                    settableFuture.set(ResultBuilder.failed(deviceInfo).build());
                }
            });
        } catch (Throwable throwable) {
            LOG.warn("start device:{} error.", this, throwable);
            settableFuture.set(ResultBuilder.failed(deviceInfo, "start error").build());
        }

        return settableFuture;
    }

    @SuppressWarnings("IllegalCatch")
    private ListenableFuture<Result<CallhomeInfo>> callhomeConnect(CallhomeInfo info, InetSocketAddress address) {
        if (simulateServer == null) {
            LOG.warn("Simulate server does not started.");
            return Futures.immediateFailedFuture(new RuntimeException("Simulate server does not started."));
        }

        try {
            SettableFuture<Result<CallhomeInfo>> settableFuture = SettableFuture.create();
            simulateServer.connect(address)
                .addListener(future -> {
                    if (future.isConnected()) {
                        settableFuture.set(ResultBuilder.success(info).build());
                    } else {
                        settableFuture.set(ResultBuilder.failed(info, "connect failed").build());
                    }

                    if (future.getException() != null) {
                        settableFuture.set(ResultBuilder.failed(info, future.getException()).build());
                    }
                });

            return settableFuture;
        } catch (Throwable throwable) {
            return Futures.immediateFuture(ResultBuilder.failed(info, throwable).build());
        }
    }

    public ListenableFuture<Result<CallhomeInfo>> callhomeConnect(String ipaddress, Integer port) {
        CallhomeInfo callhomeInfo = new CallhomeInfo();

        callhomeInfo.setCallhomeIp(ipaddress);
        callhomeInfo.setCallhomePort(port);
        callhomeInfo.setUniqueKey(uniqueKey);

        InetSocketAddress inetAddress = getInetAddress(ipaddress, port.toString());
        return callhomeConnect(callhomeInfo, inetAddress);
    }

    public void callhomeDisconnect(String ipaddress, Integer port) {
        InetSocketAddress inetAddress = getInetAddress(ipaddress, port.toString());
        callhomeDisconnect(inetAddress);
    }

    public void callhomeDisconnect(InetSocketAddress address) {
        LOG.info("disconnect callhome connection, target:{}", address);
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
