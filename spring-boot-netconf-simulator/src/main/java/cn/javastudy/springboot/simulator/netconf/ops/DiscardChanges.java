package cn.javastudy.springboot.simulator.netconf.ops;

import java.util.HashMap;
import java.util.Map;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.mdsal.connector.TransactionProvider;
import org.opendaylight.netconf.util.mapping.AbstractSingletonNetconfOperation;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DiscardChanges extends AbstractSingletonNetconfOperation {
    private static final Logger LOG = LoggerFactory.getLogger(DiscardChanges.class);
    private static final String OPERATION_NAME = "discard-changes";

    private final TransactionProvider transactionProvider;

    public DiscardChanges(final String netconfSessionIdForReporting, final TransactionProvider transactionProvider) {
        super(netconfSessionIdForReporting);
        this.transactionProvider = transactionProvider;
    }

    @Override
    protected Element handleWithNoSubsequentOperations(final Document document, final XmlElement operationElement)
            throws DocumentedException {

        try {
            transactionProvider.abortTransaction();
        } catch (final IllegalStateException e) {
            LOG.warn("Abort failed ", e);
            final Map<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ErrorTag.OPERATION_FAILED.elementBody(),
                    "Operation failed. Use 'get-config' or 'edit-config' before triggering "
                            + OPERATION_NAME + " operation");
            throw new DocumentedException(e.getMessage(), e, ErrorType.APPLICATION, ErrorTag.OPERATION_FAILED,
                    ErrorSeverity.ERROR, errorInfo);
        }
        return document.createElement(XmlNetconfConstants.OK);
    }

    @Override
    protected String getOperationName() {
        return OPERATION_NAME;
    }
}
