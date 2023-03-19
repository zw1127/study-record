package cn.javastudy.springboot.simulator.netconf.rpchandler;

import java.util.Optional;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class RpcHandlerDefault implements RpcHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RpcHandlerDefault.class);

    @Override
    public Optional<Document> getResponse(XmlElement rpcElement) {
        LOG.debug("getResponse: {}", XmlUtil.toString(rpcElement.getDomElement()));
        return Optional.empty();
    }

}
