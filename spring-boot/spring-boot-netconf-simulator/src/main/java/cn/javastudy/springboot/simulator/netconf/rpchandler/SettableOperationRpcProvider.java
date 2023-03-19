package cn.javastudy.springboot.simulator.netconf.rpchandler;

import java.util.Collections;
import java.util.Set;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.monitoring.CapabilityListener;
import org.opendaylight.netconf.mapping.api.NetconfOperation;
import org.opendaylight.netconf.mapping.api.NetconfOperationService;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;

public class SettableOperationRpcProvider implements NetconfOperationServiceFactory {

    private final RpcHandler rpcHandler;

    public SettableOperationRpcProvider(RpcHandler rpcHandler) {
        this.rpcHandler = rpcHandler;
    }

    @Override
    public Set<Capability> getCapabilities() {
        return Collections.emptySet();
    }

    @Override
    public AutoCloseable registerCapabilityListener(final CapabilityListener listener) {
        return () -> {
            //no op
        };
    }

    @Override
    public NetconfOperationService createService(final String netconfSessionIdForReporting) {
        return new SettableOperationService(rpcHandler);
    }

    private static class SettableOperationService implements NetconfOperationService {

        private final SettableRpc rpc;

        SettableOperationService(RpcHandler rpcHandler) {
            this.rpc = new SettableRpc(rpcHandler);
        }

        @Override
        public Set<NetconfOperation> getNetconfOperations() {
            return Collections.singleton(rpc);
        }

        @Override
        public void close() {
            // no op
        }
    }
}
