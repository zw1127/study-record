package cn.javastudy.springboot.simulator.netconf.rpchandler.response;

import java.util.Collections;
import java.util.List;
import org.opendaylight.netconf.api.NetconfDocumentedException;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.w3c.dom.Document;

public class ResponseErrorMessage implements Response {

    private final NetconfDocumentedException data;

    public ResponseErrorMessage(final NetconfDocumentedException data) {
        this.data = data;
    }

    @Override
    public List<NormalizedNode> getData() {
        return Collections.emptyList();
    }

    @Override
    public Document getErrorDocument() {
        return data.toXMLDocument();
    }

}
