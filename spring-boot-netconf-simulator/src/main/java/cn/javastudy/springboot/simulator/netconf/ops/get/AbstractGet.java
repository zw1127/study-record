package cn.javastudy.springboot.simulator.netconf.ops.get;

import static com.google.common.base.Preconditions.checkArgument;

import cn.javastudy.springboot.simulator.netconf.ops.Datastore;
import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Optional;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.mapping.api.NetconfOperationChainedExecution;
import org.opendaylight.netconf.util.mapping.AbstractSingletonNetconfOperation;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeWriter;
import org.opendaylight.yangtools.yang.data.api.schema.stream.YangInstanceIdentifierWriter;
import org.opendaylight.yangtools.yang.data.codec.xml.XMLStreamNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// FIXME: seal when we have JDK17+
public abstract class AbstractGet extends AbstractSingletonNetconfOperation {
    private static final XMLOutputFactory XML_OUTPUT_FACTORY;
    private static final String FILTER = "filter";

    static {
        XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();
        XML_OUTPUT_FACTORY.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    }

    // FIXME: hide this field
    protected final SchemaContextService schemaContext;
    private final FilterContentValidator validator;

    // FIXME: package-private
    protected AbstractGet(final String netconfSessionIdForReporting, final SchemaContextService schemaContext) {
        super(netconfSessionIdForReporting);
        this.schemaContext = schemaContext;
        validator = new FilterContentValidator(schemaContext);
    }

    // FIXME: hide this method
    // FIXME: throw a DocumentedException
    protected Node transformNormalizedNode(final Document document, final NormalizedNode data,
                                           final YangInstanceIdentifier dataRoot) {
        final DOMResult result = new DOMResult(document.createElement(XmlNetconfConstants.DATA_KEY));
        final XMLStreamWriter xmlWriter = getXmlStreamWriter(result);
        final EffectiveModelContext currentContext = schemaContext.getSchemaContext();

        final NormalizedNodeStreamWriter nnStreamWriter = XMLStreamNormalizedNodeStreamWriter.create(xmlWriter,
            currentContext);

        try {
            if (dataRoot.isEmpty()) {
                writeRoot(nnStreamWriter, data);
            } else {
                write(nnStreamWriter, currentContext, dataRoot.coerceParent(), data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.getNode();
    }

    @Override
    public Document handle(Document requestMessage, NetconfOperationChainedExecution subsequentOperation)
        throws DocumentedException {
        Document document = super.handle(requestMessage, subsequentOperation);

        schemaContext.processDynamicConfig(document);

        return document;
    }

    private static void write(final NormalizedNodeStreamWriter nnStreamWriter,
            final EffectiveModelContext currentContext, final YangInstanceIdentifier parent, final NormalizedNode data)
                throws IOException {
        try (var yiidWriter = YangInstanceIdentifierWriter.open(nnStreamWriter, currentContext, parent)) {
            try (var nnWriter = NormalizedNodeWriter.forStreamWriter(nnStreamWriter, true)) {
                nnWriter.write(data);
            }
        }
    }

    private static void writeRoot(final NormalizedNodeStreamWriter nnStreamWriter, final NormalizedNode data)
            throws IOException {
        checkArgument(data instanceof ContainerNode, "Unexpected root data %s", data);

        try (var nnWriter = NormalizedNodeWriter.forStreamWriter(nnStreamWriter, true)) {
            for (var child : ((ContainerNode) data).body()) {
                nnWriter.write(child);
            }
        }
    }

    private static XMLStreamWriter getXmlStreamWriter(final DOMResult result) {
        try {
            return XML_OUTPUT_FACTORY.createXMLStreamWriter(result);
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    protected Element serializeNodeWithParentStructure(final Document document, final YangInstanceIdentifier dataRoot,
                                                       final NormalizedNode node) {
        return (Element) transformNormalizedNode(document, node, dataRoot);
    }

    /**
     * Obtain data root according to filter from operation element.
     *
     * @param operationElement operation element
     * @return if filter is present and not empty returns Optional of the InstanceIdentifier to the read location
     *      in datastore. Empty filter returns Optional.absent() which should equal an empty &lt;data/&gt;
     *      container in the response. If filter is not present we want to read the entire datastore - return ROOT.
     * @throws DocumentedException if not possible to get identifier from filter
     */
    protected Optional<YangInstanceIdentifier> getDataRootFromFilter(final XmlElement operationElement)
            throws DocumentedException {
        final Optional<XmlElement> filterElement = operationElement.getOnlyChildElementOptionally(FILTER);
        if (filterElement.isPresent()) {
            if (filterElement.get().getChildElements().size() == 0) {
                return Optional.empty();
            }
            return Optional.of(getInstanceIdentifierFromFilter(filterElement.get()));
        }

        return Optional.of(YangInstanceIdentifier.empty());
    }

    @VisibleForTesting
    protected YangInstanceIdentifier getInstanceIdentifierFromFilter(final XmlElement filterElement)
            throws DocumentedException {

        if (filterElement.getChildElements().size() != 1) {
            throw new DocumentedException("Multiple filter roots not supported yet",
                    ErrorType.APPLICATION, ErrorTag.OPERATION_NOT_SUPPORTED, ErrorSeverity.ERROR);
        }

        final XmlElement element = filterElement.getOnlyChildElement();
        return validator.validate(element);
    }

    protected static final class GetConfigExecution {
        private final Optional<Datastore> datastore;

        GetConfigExecution(final Optional<Datastore> datastore) {
            this.datastore = datastore;
        }

        static GetConfigExecution fromXml(final XmlElement xml, final String operationName) throws DocumentedException {
            try {
                validateInputRpc(xml, operationName);
            } catch (final DocumentedException e) {
                throw new DocumentedException("Incorrect RPC: " + e.getMessage(), e, e.getErrorType(), e.getErrorTag(),
                        e.getErrorSeverity(), e.getErrorInfo());
            }

            final Optional<Datastore> sourceDatastore;
            try {
                sourceDatastore = parseSource(xml);
            } catch (final DocumentedException e) {
                throw new DocumentedException("Get-config source attribute error: " + e.getMessage(), e,
                        e.getErrorType(), e.getErrorTag(), e.getErrorSeverity(), e.getErrorInfo());
            }

            return new GetConfigExecution(sourceDatastore);
        }

        private static Optional<Datastore> parseSource(final XmlElement xml) throws DocumentedException {
            final Optional<XmlElement> sourceElement = xml.getOnlyChildElementOptionally(XmlNetconfConstants.SOURCE_KEY,
                    XmlNetconfConstants.URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0);
            return sourceElement.isPresent()
                    ? Optional.of(Datastore.valueOf(sourceElement.get().getOnlyChildElement().getName()))
                    : Optional.empty();
        }

        private static void validateInputRpc(final XmlElement xml, final String operationName) throws
                DocumentedException {
            xml.checkName(operationName);
            xml.checkNamespace(XmlNetconfConstants.URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0);
        }

        public Optional<Datastore> getDatastore() {
            return datastore;
        }
    }
}
