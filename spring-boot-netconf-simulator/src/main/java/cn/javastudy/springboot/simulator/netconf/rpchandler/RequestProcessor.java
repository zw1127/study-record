package cn.javastudy.springboot.simulator.netconf.rpchandler;

import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import org.opendaylight.yangtools.yang.common.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Netconf request processor.
 *
 */
public interface RequestProcessor {

    /**
     * Returns the QName identifier of the implementation.
     *
     * @return FQN a FQN
     */
    QName getIdentifier();

    /**
     * Parses the input element and do its operation around it.
     *
     * @param requestXmlElement XML RPC request element
     * @return Document a document
     */
    Document processRequest(Element requestXmlElement);

    void init(SchemaContextService schemaContextService);

}
