package cn.simulator.netconf.operate;

import cn.simulator.netconf.ops.Commit;
import cn.simulator.netconf.ops.CopyConfig;
import cn.simulator.netconf.ops.DiscardChanges;
import cn.simulator.netconf.ops.EditConfig;
import cn.simulator.netconf.ops.Lock;
import cn.simulator.netconf.ops.Unlock;
import cn.simulator.netconf.ops.Validate;
import cn.simulator.netconf.ops.get.Get;
import cn.simulator.netconf.ops.get.GetConfig;
import cn.simulator.netconf.service.SchemaContextService;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.netconf.mapping.api.NetconfOperation;
import org.opendaylight.netconf.mdsal.connector.TransactionProvider;

public class OperationProvider {

    private final Set<NetconfOperation> operations;

    OperationProvider(final String netconfSessionIdForReporting,
                      final SchemaContextService schemaContext,
                      final DOMDataBroker dataBroker) {
        TransactionProvider transactionProvider = new TransactionProvider(dataBroker, netconfSessionIdForReporting);

        this.operations = ImmutableSet.of(
            new Commit(netconfSessionIdForReporting, transactionProvider),
            new DiscardChanges(netconfSessionIdForReporting, transactionProvider),
            new EditConfig(netconfSessionIdForReporting, schemaContext, transactionProvider),
            new CopyConfig(netconfSessionIdForReporting, schemaContext, transactionProvider),
            new Get(netconfSessionIdForReporting, schemaContext, transactionProvider),
            new GetConfig(netconfSessionIdForReporting, schemaContext, transactionProvider),
            new Lock(netconfSessionIdForReporting),
            new Unlock(netconfSessionIdForReporting),
            new Validate(netconfSessionIdForReporting, transactionProvider)
        );
    }

    Set<NetconfOperation> getOperations() {
        return operations;
    }
}
