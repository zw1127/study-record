package cn.javastudy.springboot.simulator.netconf.monitoring;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.SchemaFormat;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.Yang;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.netconf.state.schemas.Schema;

final class MonitoringSchema {

    private final Schema schema;

    MonitoringSchema(final Schema schema) {
        this.schema = schema;
    }

    @XmlElement(name = "identifier")
    public String getIdentifier() {
        return schema.getIdentifier();
    }

    @XmlElement(name = "namespace")
    public String getNamespace() {
        return schema.getNamespace().getValue();
    }

    @XmlElement(name = "location")
    public Collection<String> getLocation() {
        return Collections2.transform(schema.getLocation(), input -> input.getEnumeration().toString());
    }

    @XmlElement(name = "version")
    public String getVersion() {
        return schema.getVersion();
    }

    @XmlElement(name = "format")
    public String getFormat() {
        Class<? extends SchemaFormat> format = schema.getFormat();
        Preconditions.checkState(format == Yang.class, "Only yang format permitted, but was %s", format);
        return "yang";
    }
}