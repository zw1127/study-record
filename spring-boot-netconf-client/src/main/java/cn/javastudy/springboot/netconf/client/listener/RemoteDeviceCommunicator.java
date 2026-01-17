package cn.javastudy.springboot.netconf.client.listener;

import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.netconf.api.NetconfMessage;
import org.opendaylight.yangtools.yang.common.RpcResult;

public interface RemoteDeviceCommunicator extends AutoCloseable {

    /**
     * Send request message to current client session.
     *
     * @param message {@link NetconfMessage} to be sent
     * @return A {@link ListenableFuture} which completes with result of sending given message
     * represented by {@link RpcResult}
     */
    ListenableFuture<RpcResult<NetconfMessage>> sendRequest(NetconfMessage message);

    @Override
    void close();
}
