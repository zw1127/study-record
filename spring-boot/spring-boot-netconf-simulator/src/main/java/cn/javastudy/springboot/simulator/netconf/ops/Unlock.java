package cn.javastudy.springboot.simulator.netconf.ops;

import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.util.mapping.AbstractSingletonNetconfOperation;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Unlock extends AbstractSingletonNetconfOperation {
    private static final Logger LOG = LoggerFactory.getLogger(Unlock.class);
    private static final String OPERATION_NAME = "unlock";

    public Unlock(final String netconfSessionIdForReporting) {
        super(netconfSessionIdForReporting);
    }

    @Override
    protected Element handleWithNoSubsequentOperations(final Document document, final XmlElement operationElement)
            throws DocumentedException {
        final Datastore targetDatastore = Lock.extractTargetParameter(operationElement);
        if (targetDatastore == Datastore.candidate) {
            LOG.debug("Unlocking candidate datastore on session: {}", getNetconfSessionIdForReporting());
            return document.createElement(XmlNetconfConstants.OK);
        }

        throw new DocumentedException("Unable to unlock " + targetDatastore + " datastore",
                ErrorType.APPLICATION, ErrorTag.OPERATION_NOT_SUPPORTED, ErrorSeverity.ERROR);
    }

    @Override
    protected String getOperationName() {
        return OPERATION_NAME;
    }
}
