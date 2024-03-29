package cn.javastudy.springboot.simulator.netconf.ops.get;

import cn.javastudy.springboot.simulator.netconf.ops.Datastore;
import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.mdsal.dom.api.DOMDataTreeReadWriteTransaction;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.mdsal.connector.TransactionProvider;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Get extends AbstractGet {

    private static final Logger LOG = LoggerFactory.getLogger(Get.class);

    private static final String OPERATION_NAME = "get";
    private final TransactionProvider transactionProvider;

    public Get(final String netconfSessionIdForReporting, final SchemaContextService schemaContextService,
               final TransactionProvider transactionProvider) {
        super(netconfSessionIdForReporting, schemaContextService);
        this.transactionProvider = transactionProvider;
    }

    @Override
    protected Element handleWithNoSubsequentOperations(final Document document, final XmlElement operationElement)
            throws DocumentedException {

        final Optional<YangInstanceIdentifier> dataRootOptional = getDataRootFromFilter(operationElement);
        if (dataRootOptional.isEmpty()) {
            return document.createElement(XmlNetconfConstants.DATA_KEY);
        }

        final YangInstanceIdentifier dataRoot = dataRootOptional.get();

        final DOMDataTreeReadWriteTransaction rwTx = getTransaction(Datastore.running);
        try {
            final Optional<NormalizedNode> normalizedNodeOptional = rwTx.read(
                    LogicalDatastoreType.OPERATIONAL, dataRoot).get();
            transactionProvider.abortRunningTransaction(rwTx);

            if (normalizedNodeOptional.isEmpty()) {
                return document.createElement(XmlNetconfConstants.DATA_KEY);
            }

            return serializeNodeWithParentStructure(document, dataRoot, normalizedNodeOptional.get());
        } catch (final InterruptedException | ExecutionException e) {
            LOG.warn("Unable to read data: {}", dataRoot, e);
            throw new IllegalStateException("Unable to read data " + dataRoot, e);
        }
    }

    private DOMDataTreeReadWriteTransaction getTransaction(final Datastore datastore) throws DocumentedException {
        if (datastore == Datastore.candidate) {
            return transactionProvider.getOrCreateTransaction();
        } else if (datastore == Datastore.running) {
            return transactionProvider.createRunningTransaction();
        }
        throw new DocumentedException("Incorrect Datastore: ", ErrorType.PROTOCOL, ErrorTag.BAD_ELEMENT,
                ErrorSeverity.ERROR);
    }

    @Override
    protected String getOperationName() {
        return OPERATION_NAME;
    }
}
