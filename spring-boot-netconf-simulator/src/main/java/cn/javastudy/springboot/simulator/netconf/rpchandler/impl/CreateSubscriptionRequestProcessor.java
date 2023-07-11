package cn.javastudy.springboot.simulator.netconf.rpchandler.impl;

import static org.opendaylight.netconf.api.xml.XmlNetconfConstants.MESSAGE_ID;
import static org.opendaylight.netconf.api.xml.XmlNetconfConstants.OK;
import static org.opendaylight.netconf.api.xml.XmlNetconfConstants.RPC_REPLY_KEY;
import static org.opendaylight.netconf.api.xml.XmlNetconfConstants.URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0;

import cn.javastudy.springboot.simulator.netconf.rpchandler.BaseRequestProcessor;
import cn.javastudy.springboot.simulator.netconf.rpchandler.response.Response;
import cn.javastudy.springboot.simulator.netconf.rpchandler.response.ResponseData;
import cn.javastudy.springboot.simulator.netconf.utils.XmlUtils;
import io.lighty.codecs.util.exception.SerializationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.notification._1._0.rev080714.CreateSubscriptionInput;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CreateSubscriptionRequestProcessor extends BaseRequestProcessor {

    private static final String CREATE_SUBSCRIPTION_RPC_NAME = "create-subscription";
    private static final Logger LOG = LoggerFactory.getLogger(CreateSubscriptionRequestProcessor.class);
    private String messageId;

    @Override
    public QName getIdentifier() {
        return QName.create(CreateSubscriptionInput.QNAME.getNamespace(), CREATE_SUBSCRIPTION_RPC_NAME);
    }

    @Override
    protected CompletableFuture<Response> execute(Element requestXmlElement) {
        messageId = requestXmlElement.getAttribute(MESSAGE_ID);
        final CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        responseFuture.complete(new ResponseData(Collections.emptyList()));
        return responseFuture;
    }

    @SuppressWarnings("checkstyle:AvoidHidingCauseException")
    @Override
    protected Document wrapToFinalDocumentReply(
        List<NormalizedNode> responseOutput) throws ParserConfigurationException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document newDocument = builder.newDocument();

            // convert normalized nodes to xml nodes
            List<Node> outputNodes = List.of(newDocument.createElementNS(URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0, OK));
            List<Node> wrappedOutputNodes = new ArrayList<>();
            outputNodes.forEach(outputNode -> wrappedOutputNodes.add(newDocument.importNode(outputNode, true)));

            Element rpcReply = newDocument.createElementNS(URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0, RPC_REPLY_KEY);
            rpcReply.setAttribute(MESSAGE_ID, messageId);
            wrappedOutputNodes.forEach(rpcReply::appendChild);
            newDocument.appendChild(rpcReply);

            String formattedResponse = XmlUtils.toString(newDocument.getDocumentElement());
            LOG.debug("Response: \n{}.", formattedResponse);
            return newDocument;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error while creating create-subscription reply XML document");
        }
    }

    @Override
    protected String convertNormalizedNodeToXmlString(NormalizedNode normalizedNode) throws SerializationException {
        throw new IllegalStateException("This method should not be called!");
    }
}
