package cn.simulator.netconf.provider;

import io.netty.util.Timer;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.opendaylight.netconf.api.monitoring.NetconfMonitoringService;
import org.opendaylight.netconf.impl.NetconfServerSessionNegotiatorFactory;
import org.opendaylight.netconf.impl.SessionIdProvider;
import org.opendaylight.netconf.mapping.api.NetconfOperationService;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulateNegotiationFactory extends NetconfServerSessionNegotiatorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SimulateNegotiationFactory.class);

    private final Map<SocketAddress, NetconfOperationService> cachedOperationServices = new HashMap<>();

    public SimulateNegotiationFactory(final Timer timer, final NetconfOperationServiceFactory operationServiceFatory,
                                      final SessionIdProvider idProvider, final long connectionTimeoutMillis,
                                      final NetconfMonitoringService monitoringService,
                                      final Set<String> baseCapabilities) {
        super(timer, operationServiceFatory, idProvider, connectionTimeoutMillis, monitoringService, baseCapabilities);
    }

    @Override
    protected NetconfOperationService getOperationServiceForAddress(
        final String netconfSessionIdForReporting, final SocketAddress socketAddress) {
        if (cachedOperationServices.containsKey(socketAddress)) {
            LOG.debug("Session {}: Getting cached operation service factory for test tool device on address {}",
                netconfSessionIdForReporting, socketAddress);
            return cachedOperationServices.get(socketAddress);
        } else {
            NetconfOperationService service = getOperationServiceFactory().createService(netconfSessionIdForReporting);
            cachedOperationServices.put(socketAddress, service);
            LOG.debug("Session {}: Creating new operation service factory for test tool device on address {}",
                netconfSessionIdForReporting, socketAddress);
            return service;
        }
    }
}
