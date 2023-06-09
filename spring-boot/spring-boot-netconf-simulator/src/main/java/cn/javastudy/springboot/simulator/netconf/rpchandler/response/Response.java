package cn.javastudy.springboot.simulator.netconf.rpchandler.response;

import java.util.List;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.w3c.dom.Document;

public interface Response {

    /**
     * Data returned according to successful request.
     *
     * @return data
     */
    List<NormalizedNode> getData();

    /**
     * Specific Error returned according to unsuccessful requests.
     *
     * @return error
     */
    Document getErrorDocument();

}
