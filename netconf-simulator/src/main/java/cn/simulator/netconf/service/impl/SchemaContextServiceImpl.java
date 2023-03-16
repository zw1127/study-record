package cn.simulator.netconf.service.impl;

import static cn.simulator.netconf.utils.Constants.SIMULATE_HOME;
import static cn.simulator.netconf.utils.Constants.YANG_SCHEMAS_PATH;

import cn.simulator.netconf.service.SchemaContextService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.NetconfDocumentedException;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.QNameModule;
import org.opendaylight.yangtools.yang.common.XMLNamespace;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.codec.xml.XmlParserStream;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizedNodeResult;
import org.opendaylight.yangtools.yang.data.util.DataSchemaContextTree;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.SchemaTreeInference;
import org.opendaylight.yangtools.yang.model.api.stmt.ModuleEffectiveStatement;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;
import org.opendaylight.yangtools.yang.test.util.YangParserTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

public class SchemaContextServiceImpl implements SchemaContextService {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaContextServiceImpl.class);

    private final Map<String, QNameModule> namespaceToQNameModule = new ConcurrentHashMap<>();

    private EffectiveModelContext schemaContext;
    private DataSchemaContextTree dataContextTree;

    @PostConstruct
    public void init() {
        LOG.info("start init schemaContext...");
        final File directoryPath;
        String homePath = System.getProperty(SIMULATE_HOME);
        if (org.springframework.util.StringUtils.hasLength(homePath)) {
            directoryPath = Paths.get(homePath, YANG_SCHEMAS_PATH).toFile();
        } else {
            try {
                directoryPath = ResourceUtils.getFile("classpath:" + YANG_SCHEMAS_PATH);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        this.schemaContext = YangParserTestUtils.parseYangResourceDirectory(directoryPath.getPath());
        Map<QNameModule, ModuleEffectiveStatement> effectiveStatementMap = schemaContext.getModuleStatements();
        if (!effectiveStatementMap.isEmpty()) {
            for (Map.Entry<QNameModule, ModuleEffectiveStatement> entry : effectiveStatementMap.entrySet()) {
                namespaceToQNameModule.put(entry.getKey().getNamespace().toString(), entry.getKey());
            }
        }
        schemaContext.getModules();
        this.dataContextTree = DataSchemaContextTree.from(schemaContext);
        LOG.info("init schemaContext successful...");
    }

    @Override
    public QNameModule findQNameModuleByNamespace(String namespace) {
        return namespaceToQNameModule.get(namespace);
    }

    @Override
    public SchemaTreeInference getSchemaNodeFromNamespace(XmlElement element) throws DocumentedException {
        String namespace = element.getNamespace();

        final XMLNamespace ns;
        try {
            ns = XMLNamespace.of(namespace);
        } catch (final IllegalArgumentException e) {
            throw new NetconfDocumentedException("Unable to create URI for namespace : " + namespace, e,
                ErrorType.APPLICATION, ErrorTag.INVALID_VALUE, ErrorSeverity.ERROR);
        }

        // Returns module with newest revision since findModuleByNamespace returns a set of modules and we only
        // need the newest one
        final Iterator<? extends @NonNull Module> it = schemaContext.findModules(ns).iterator();
        if (!it.hasNext()) {
            // No module is present with this namespace
            throw new NetconfDocumentedException("Unable to find module by namespace: " + namespace,
                ErrorType.APPLICATION, ErrorTag.UNKNOWN_NAMESPACE, ErrorSeverity.ERROR);
        }

        final Module module = it.next();
        final SchemaInferenceStack stack = SchemaInferenceStack.of(schemaContext);
        final String elementName = element.getName();
        try {
            // FIXME: This is a bit suspect. The element is formed using XML encoding, hence it corresponds to
            //        enterDataTree(). But then we use the result of this method to create a NormalizedNode tree,
            //        which contains ChoiceNode. This needs to be tested with something like to following:
            //
            //        module mod {
            //          choice foo {
            //            case bar {
            //              leaf baz {
            //                type string;
            //              }
            //            }
            //          }
            //        }
            stack.enterSchemaTree(QName.create(module.getQNameModule(), elementName));
        } catch (IllegalArgumentException e) {
            throw new DocumentedException(
                "Unable to find node " + elementName + " with namespace: " + namespace + " in module: " + module, e,
                ErrorType.APPLICATION, ErrorTag.UNKNOWN_NAMESPACE, ErrorSeverity.ERROR);
        }

        return stack.toSchemaTreeInference();
    }

    @Override
    public void parseIntoNormalizedNode(SchemaTreeInference inference,
                                        XmlElement element,
                                        NormalizedNodeStreamWriter writer) throws DocumentedException {
        final var path = inference.statementPath();
        final var schemaNode = path.get(path.size() - 1);
        if (!(schemaNode instanceof ContainerSchemaNode) && !(schemaNode instanceof ListSchemaNode)) {
            // This should never happen since any edit operation on any other node type
            // should not be possible nor makes sense
            LOG.debug("DataNode from module is not ContainerSchemaNode nor ListSchemaNode, aborting..");
            throw new UnsupportedOperationException("implement exception if parse fails");
        }

        final XmlParserStream xmlParser = XmlParserStream.create(writer, inference);
        try {
            xmlParser.traverse(new DOMSource(element.getDomElement()));
        } catch (final XMLStreamException | URISyntaxException | IOException | SAXException ex) {
            throw new NetconfDocumentedException("Error parsing input: " + ex.getMessage(), ex,
                ErrorType.PROTOCOL, ErrorTag.MALFORMED_MESSAGE, ErrorSeverity.ERROR);
        }
    }

    @Override
    public NormalizedNode parseToNormalizedNode(XmlElement element) throws DocumentedException {
        SchemaTreeInference schemaTreeInference = getSchemaNodeFromNamespace(element);

        NormalizedNodeResult result = new NormalizedNodeResult();
        NormalizedNodeStreamWriter streamWriter = ImmutableNormalizedNodeStreamWriter.from(result);

        parseIntoNormalizedNode(schemaTreeInference, element, streamWriter);
        return result.getResult();
    }

    @Override
    public DOMResult writeNormalizedNode(NormalizedNode normalized, PathArgument pathArgument) {
        return null;
    }
}
