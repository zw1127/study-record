package cn.simulator.netconf.device;

import cn.simulator.netconf.ssh.SshProxySimulateServer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

public class NetconfSimulateDevice {

    private SshProxySimulateServer simulateServer;

    public NetconfSimulateDevice(final NioEventLoopGroup nettyThreadgroup,
                                 final HashedWheelTimer hashedWheelTimer,
                                 final ExecutorService nioExecutor) {

    }

    public void start() {

    }

    public void callhomeConnect(InetSocketAddress address) {

    }
}
