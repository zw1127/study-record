package cn.javastudy.springboot.simulator.netconf.operate;

import static java.util.Objects.requireNonNull;

import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import java.util.Collections;
import java.util.Set;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.monitoring.CapabilityListener;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactoryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryOperationServiceFactory implements NetconfOperationServiceFactory, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryOperationServiceFactory.class);

    private final DOMDataBroker dataBroker;
    private final SchemaContextService schemaContextService;
    private final NetconfOperationServiceFactoryListener netconfOperationServiceFactoryListener;
    private final Set<Capability> capabilities;

    public InMemoryOperationServiceFactory(
        final Set<Capability> capabilities,
        final SchemaContextService schemaContextService,
        final NetconfOperationServiceFactoryListener netconfOperationServiceFactoryListener,
        final DOMDataBroker dataBroker) {

        this.capabilities = capabilities;
        this.dataBroker = dataBroker;
        this.schemaContextService = schemaContextService;
        this.netconfOperationServiceFactoryListener = netconfOperationServiceFactoryListener;
        this.netconfOperationServiceFactoryListener.onAddNetconfOperationServiceFactory(this);
    }

    @Override
    public InMemoryOperationService createService(final String netconfSessionIdForReporting) {
        requireNonNull(dataBroker, "MD-SAL provider not yet initialized");
        return new InMemoryOperationService(schemaContextService, netconfSessionIdForReporting, dataBroker);
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    @Override
    public void close() {
        try {
            if (netconfOperationServiceFactoryListener != null) {
                netconfOperationServiceFactoryListener.onRemoveNetconfOperationServiceFactory(this);
            }
        } catch (Exception e) {
            LOG.error("Failed to close resources correctly - ignore", e);
        }
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
