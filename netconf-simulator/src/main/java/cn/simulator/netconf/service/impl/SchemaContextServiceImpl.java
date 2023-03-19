package cn.simulator.netconf.service.impl;

import static cn.simulator.netconf.utils.Constants.DEFAULT_SUPPORTED_MODULE_INFOS;
import static cn.simulator.netconf.utils.Constants.SIMULATE_HOME;
import static cn.simulator.netconf.utils.Constants.YANG_SCHEMAS_PATH;

import cn.simulator.netconf.schemacache.SchemaSourceCache;
import cn.simulator.netconf.service.SchemaContextService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.runtime.api.ModuleInfoSnapshot;
import org.opendaylight.mdsal.binding.runtime.spi.ModuleInfoSnapshotResolver;
import org.opendaylight.mdsal.dom.api.DOMSchemaService;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.NetconfDocumentedException;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.capability.YangModuleCapability;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.yangtools.concepts.ObjectRegistration;
import org.opendaylight.yangtools.yang.binding.YangModuleInfo;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.QNameModule;
import org.opendaylight.yangtools.yang.common.XMLNamespace;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.codec.xml.XmlParserStream;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizedNodeResult;
import org.opendaylight.yangtools.yang.data.util.DataSchemaContextTree;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.ModuleLike;
import org.opendaylight.yangtools.yang.model.api.SchemaTreeInference;
import org.opendaylight.yangtools.yang.model.api.Submodule;
import org.opendaylight.yangtools.yang.model.repo.api.RevisionSourceIdentifier;
import org.opendaylight.yangtools.yang.model.repo.api.SchemaSourceRepresentation;
import org.opendaylight.yangtools.yang.model.repo.api.SourceIdentifier;
import org.opendaylight.yangtools.yang.model.repo.api.YangTextSchemaSource;
import org.opendaylight.yangtools.yang.model.repo.fs.FilesystemSchemaSourceCache;
import org.opendaylight.yangtools.yang.model.repo.spi.PotentialSchemaSource;
import org.opendaylight.yangtools.yang.model.repo.spi.SchemaListenerRegistration;
import org.opendaylight.yangtools.yang.model.repo.spi.SchemaSourceListener;
import org.opendaylight.yangtools.yang.model.repo.spi.SchemaSourceProvider;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;
import org.opendaylight.yangtools.yang.parser.impl.DefaultYangParserFactory;
import org.opendaylight.yangtools.yang.parser.repo.SharedSchemaRepository;
import org.opendaylight.yangtools.yang.parser.rfc7950.repo.TextToIRTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

public class SchemaContextServiceImpl implements SchemaContextService {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaContextServiceImpl.class);

    private final Map<String, QNameModule> namespaceToQNameModule = new ConcurrentHashMap<>();

    private final List<SchemaListenerRegistration> registrations = new CopyOnWriteArrayList<>();

    private final Set<Capability> capabilities = new HashSet<>();

    private EffectiveModelContext schemaContext;
    private DataSchemaContextTree dataContextTree;

    private SchemaSourceProvider<YangTextSchemaSource> sourceProvider;

    private DefaultYangParserFactory yangParserFactory;

    private ModuleInfoSnapshot moduleInfoSnapshot;
    private ModuleInfoSnapshotResolver snapshotResolver;
    private DOMSchemaService schemaService;

    private List<ObjectRegistration<YangModuleInfo>> modelsRegistration = new ArrayList<>();

    private File yangSchemaPath;

    @PostConstruct
    public void init() {
        LOG.info("{} starting...", getClass());
        String homePath = System.getProperty(SIMULATE_HOME);
        if (StringUtils.hasLength(homePath)) {
            yangSchemaPath = Paths.get(homePath, YANG_SCHEMAS_PATH).toFile();
        } else {
            try {
                yangSchemaPath = ResourceUtils.getFile("classpath:" + YANG_SCHEMAS_PATH);
            } catch (FileNotFoundException e) {
                LOG.warn("load yang resource file error.", e);
                throw new RuntimeException(e);
            }
        }

        LOG.info("start init schemaContext..., yang schema path:{}", yangSchemaPath);
        initSchemaContext();
        LOG.info("init schemaContext successful...");
        schemaContext.getModules();
        this.dataContextTree = DataSchemaContextTree.from(schemaContext);
        LOG.info("{} init successful.", getClass());
    }

    private void initSchemaContext() {
        SharedSchemaRepository consumer = new SharedSchemaRepository("netconf-simulator");

        Set<SourceIdentifier> loadedSources = new HashSet<>();

        registrations.add(consumer.registerSchemaSourceListener(TextToIRTransformer.create(consumer, consumer)));
        registrations.add(consumer.registerSchemaSourceListener(new SchemaSourceListener() {
            @Override
            public void schemaSourceEncountered(final SchemaSourceRepresentation schemaSourceRepresentation) {

            }

            @Override
            public void schemaSourceRegistered(final Iterable<PotentialSchemaSource<?>> potentialSchemaSources) {
                for (final PotentialSchemaSource<?> potentialSchemaSource : potentialSchemaSources) {
                    loadedSources.add(potentialSchemaSource.getSourceIdentifier());
                }
            }

            @Override
            public void schemaSourceUnregistered(final PotentialSchemaSource<?> potentialSchemaSource) {

            }
        }));

        // 加载yang schema目录下的yang module
        FilesystemSchemaSourceCache<YangTextSchemaSource> filesystemCache = new FilesystemSchemaSourceCache<>(
            consumer, YangTextSchemaSource.class, yangSchemaPath);
        registrations.add(consumer.registerSchemaSourceListener(filesystemCache));

        SchemaSourceCache<YangTextSchemaSource> schemaSourceCache = new SchemaSourceCache<>(
            consumer, YangTextSchemaSource.class, DEFAULT_SUPPORTED_MODULE_INFOS);
        registrations.add(consumer.registerSchemaSourceListener(schemaSourceCache));

        try {
            //necessary for creating mdsal data stores and operations
            schemaContext = consumer.createEffectiveModelContextFactory()
                .createEffectiveModelContext(loadedSources).get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new IllegalStateException(
                "Cannot parse schema context. Please read stack trace and check YANG files in schema directory.", e);
        }

        for (final Module module : schemaContext.getModules()) {
            for (final Submodule subModule : module.getSubmodules()) {
                addModuleCapability(consumer, capabilities, subModule);
            }
            addModuleCapability(consumer, capabilities, module);
        }
        LOG.info("load capabilities successful, capabilities size:{}", capabilities.size());

        sourceProvider = sourceIdentifier -> consumer.getSchemaSource(sourceIdentifier, YangTextSchemaSource.class);
    }

    private static void addModuleCapability(final SharedSchemaRepository consumer, final Set<Capability> capabilities,
                                            final ModuleLike module) {
        final SourceIdentifier moduleSourceIdentifier = RevisionSourceIdentifier.create(module.getName(),
            module.getRevision());
        try {
            final String moduleContent = new String(
                consumer.getSchemaSource(moduleSourceIdentifier, YangTextSchemaSource.class).get().read());
            capabilities.add(new YangModuleCapability(module, moduleContent));
            //IOException would be thrown in creating SchemaContext already
        } catch (final ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException("Cannot retrieve schema source for module "
                + moduleSourceIdentifier.toString() + " from schema repository", e);
        }
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

    @Override
    public EffectiveModelContext getSchemaContext() {
        return this.schemaContext;
    }

    @Override
    public SchemaSourceProvider<YangTextSchemaSource> getSourceProvider() {
        return sourceProvider;
    }

    @Override
    public File schemaDirectory() {
        return this.yangSchemaPath;
    }

    @Override
    public Set<Capability> supportedCapabilities() {
        return Set.copyOf(capabilities);
    }

    @PreDestroy
    public void stop() {
        namespaceToQNameModule.clear();
    }

}
