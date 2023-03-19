package cn.simulator.netconf.monitoring;

import java.util.Set;
import org.opendaylight.controller.config.yang.netconf.mdsal.monitoring.GetSchema;
import org.opendaylight.netconf.api.monitoring.NetconfMonitoringService;
import org.opendaylight.netconf.mapping.api.NetconfOperation;
import org.opendaylight.netconf.mapping.api.NetconfOperationService;

public class NetconfMonitoringOperationService implements NetconfOperationService {
    private final NetconfMonitoringService monitor;

    public NetconfMonitoringOperationService(final NetconfMonitoringService monitor) {
        this.monitor = monitor;
    }

    @Override
    public Set<NetconfOperation> getNetconfOperations() {
        return Set.of(new Get(monitor), new GetSchema("testtool-session", monitor));
    }

    @Override
    public void close() {
    }

}
