package cn.javastudy.springboot.simulator.netconf.monitoring;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.controller.config.yang.netconf.mdsal.monitoring.GetSchema;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.monitoring.CapabilityListener;
import org.opendaylight.netconf.api.monitoring.NetconfMonitoringService;
import org.opendaylight.netconf.mapping.api.NetconfOperation;
import org.opendaylight.netconf.mapping.api.NetconfOperationService;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;
import org.opendaylight.yangtools.concepts.Registration;

public class NetconfMonitoringOperationServiceFactory implements NetconfOperationServiceFactory, AutoCloseable {

    private final @NonNull NetconfMonitoringService netconfMonitoringService;

    public NetconfMonitoringOperationServiceFactory(final NetconfMonitoringService netconfMonitoringService) {
        this.netconfMonitoringService = requireNonNull(netconfMonitoringService);
    }

    @Override
    public NetconfOperationService createService(final String netconfSessionIdForReporting) {
        return new NetconfOperationService() {
            @Override
            public Set<NetconfOperation> getNetconfOperations() {
                return Set.of(new GetSchema(netconfSessionIdForReporting, netconfMonitoringService));
            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    public Set<Capability> getCapabilities() {
        return Set.of();
    }

    @Override
    public Registration registerCapabilityListener(final CapabilityListener listener) {
        return () -> {
        };
    }

    @Override
    public void close() {
    }
}

