package cn.javastudy.springboot.simulator.netconf.operate;

import static java.util.Objects.requireNonNull;

import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import java.util.Collections;
import java.util.Set;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.monitoring.CapabilityListener;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;

public class InMemoryOperationServiceFactory implements NetconfOperationServiceFactory, AutoCloseable {

    private final DOMDataBroker dataBroker;
    private final SchemaContextService schemaContextService;
    private final Set<Capability> capabilities;

    public InMemoryOperationServiceFactory(
        final Set<Capability> capabilities,
        final SchemaContextService schemaContextService,
        final DOMDataBroker dataBroker) {

        this.capabilities = capabilities;
        this.dataBroker = dataBroker;
        this.schemaContextService = schemaContextService;
    }

    @Override
    public InMemoryOperationService createService(final String netconfSessionIdForReporting) {
        requireNonNull(dataBroker, "MD-SAL provider not yet initialized");
        return new InMemoryOperationService(schemaContextService, netconfSessionIdForReporting, dataBroker);
    }

    @Override
    public void close() {
    }

    @Override
    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    @Override
    public AutoCloseable registerCapabilityListener(final CapabilityListener listener) {
        listener.onCapabilitiesChanged(getCapabilities(), Collections.emptySet());
        return () -> {
        };
    }
}
