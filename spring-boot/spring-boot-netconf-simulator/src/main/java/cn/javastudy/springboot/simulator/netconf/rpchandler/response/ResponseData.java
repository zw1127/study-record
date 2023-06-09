package cn.javastudy.springboot.simulator.netconf.rpchandler.response;

import java.util.List;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.w3c.dom.Document;

public class ResponseData implements Response {

    private final List<NormalizedNode> data;

    public ResponseData(final List<NormalizedNode> data) {
        this.data = data;
    }

    @Override
    public List<NormalizedNode> getData() {
        return data;
    }

    @Override
    public Document getErrorDocument() {
        return null;
    }
}
