package cn.javastudy.springboot.simulator.netconf.rpchandler;

import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import cn.javastudy.springboot.simulator.netconf.utils.XmlUtils;
import java.util.Map;
import java.util.Optional;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.yangtools.yang.common.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RpcHandlerImpl implements RpcHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RpcHandlerImpl.class);

    private final Map<QName, RequestProcessor> cache;

    public RpcHandlerImpl(final SchemaContextService schemaContextService, final Map<QName, RequestProcessor> cache) {
        this.cache = cache;
        this.cache.values().forEach(rp -> rp.init(schemaContextService));
    }

    @Override
    public Optional<Document> getResponse(final XmlElement rpcElement) {
        final Element element = rpcElement.getDomElement();
        final String formattedRequest = XmlUtils.toString(element);
        LOG.debug("Received get request with payload:\n{} ", formattedRequest);
        return getProcessorForRequest(element)
            .map(requestProcessor -> requestProcessor.processRequest(element));
    }

    private Optional<RequestProcessor> getProcessorForRequest(final Element element) {
        final String namespace = element.getNamespaceURI();
        final String localName = element.getLocalName();
        final RequestProcessor foundProcessor = this.cache.get(QName.create(namespace, localName));
        return Optional.ofNullable(foundProcessor);
    }

}
