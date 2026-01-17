package cn.javastudy.springboot.netconf.client.entity;

import java.net.InetSocketAddress;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Host;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.HostBuilder;

@Getter
@EqualsAndHashCode
public final class RemoteDeviceId {

    private final String name;
    private final InetSocketAddress address;
    private final Host host;

    public RemoteDeviceId(final String name, final InetSocketAddress address) {
        this.name = name;
        this.address = address;
        this.host = buildHost();
    }

    private Host buildHost() {
        return HostBuilder.getDefaultInstance(address.getHostString());
    }

    @Override
    public String toString() {
        return "RemoteDevice{name:" + name + ": address:" + address + '}';
    }

}
