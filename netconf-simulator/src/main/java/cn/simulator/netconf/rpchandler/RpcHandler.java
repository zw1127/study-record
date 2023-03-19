package cn.simulator.netconf.rpchandler;

import java.util.Optional;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.w3c.dom.Document;

public interface RpcHandler {

    Optional<Document> getResponse(XmlElement rpcElement);

}
