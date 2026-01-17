package cn.javastudy.springboot.netconf.client.service.impl;

import cn.javastudy.springboot.netconf.client.service.NetconfServices;
import io.netty.util.concurrent.Future;
import org.opendaylight.netconf.client.NetconfClientDispatcher;
import org.opendaylight.netconf.client.NetconfClientDispatcherImpl;
import org.opendaylight.netconf.client.NetconfClientSession;
import org.opendaylight.netconf.client.conf.NetconfClientConfiguration;
import org.opendaylight.netconf.client.conf.NetconfReconnectingClientConfiguration;
import org.opendaylight.netconf.nettyutil.ReconnectFuture;

public class NetconfClientDispatcherDelegateImpl implements NetconfClientDispatcher {

    private final NetconfClientDispatcherImpl delegate;

    public NetconfClientDispatcherDelegateImpl(NetconfServices netconfServices) {
        this.delegate = new NetconfClientDispatcherImpl(netconfServices.getBossGroup(),
            netconfServices.getWorkerGroup(), netconfServices.getTimer());
    }

    @Override
    public Future<NetconfClientSession> createClient(NetconfClientConfiguration clientConfiguration) {
        return delegate.createClient(clientConfiguration);
    }

    @Override
    public ReconnectFuture createReconnectingClient(NetconfReconnectingClientConfiguration clientConfiguration) {
        return delegate.createReconnectingClient(clientConfiguration);
    }
}
