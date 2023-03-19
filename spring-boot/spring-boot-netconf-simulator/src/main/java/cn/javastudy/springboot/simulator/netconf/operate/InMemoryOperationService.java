package cn.javastudy.springboot.simulator.netconf.operate;

import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import java.util.Set;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.netconf.mapping.api.NetconfOperation;
import org.opendaylight.netconf.mapping.api.NetconfOperationService;

public class InMemoryOperationService implements NetconfOperationService {

    private final OperationProvider operationProvider;

    public InMemoryOperationService(final SchemaContextService schemaContext,
                                    final String netconfSessionIdForReporting,
                                    final DOMDataBroker dataBroker) {
        this.operationProvider = new OperationProvider(netconfSessionIdForReporting, schemaContext, dataBroker);
    }

    @Override
    public Set<NetconfOperation> getNetconfOperations() {
        return operationProvider.getOperations();
    }

    @Override
    public void close() {
    }
}
