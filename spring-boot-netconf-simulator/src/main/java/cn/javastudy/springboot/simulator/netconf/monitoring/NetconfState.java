package cn.javastudy.springboot.simulator.netconf.monitoring;

import com.google.common.collect.Collections2;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.opendaylight.netconf.api.monitoring.NetconfMonitoringService;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.netconf.state.Schemas;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.netconf.state.Sessions;

@XmlRootElement(name = MonitoringConstants.NETCONF_MONITORING_XML_ROOT_ELEMENT)
public final class NetconfState {

    private Schemas schemas;
    private Sessions sessions;

    public NetconfState(final NetconfMonitoringService monitoringService) {
        this.sessions = monitoringService.getSessions();
        this.schemas = monitoringService.getSchemas();
    }

    public NetconfState() {

    }

    @XmlElementWrapper(name = "schemas")
    @XmlElement(name = "schema")
    public Collection<MonitoringSchema> getSchemas() {
        return Collections2.transform(schemas.nonnullSchema().values(), MonitoringSchema::new);
    }

    @XmlElementWrapper(name = "sessions")
    @XmlElement(name = "session")
    public Collection<MonitoringSession> getSessions() {
        return Collections2.transform(sessions.nonnullSession().values(), MonitoringSession::new);
    }
}
