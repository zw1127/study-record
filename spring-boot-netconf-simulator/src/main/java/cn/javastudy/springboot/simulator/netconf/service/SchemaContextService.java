package cn.javastudy.springboot.simulator.netconf.service;

import io.lighty.codecs.util.XmlNodeConverter;
import java.io.File;
import java.util.Set;
import javax.xml.transform.dom.DOMResult;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.QNameModule;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.SchemaTreeInference;
import org.opendaylight.yangtools.yang.model.repo.api.YangTextSchemaSource;
import org.opendaylight.yangtools.yang.model.repo.spi.SchemaSourceProvider;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;
import org.w3c.dom.Document;

public interface SchemaContextService {

    /**
     * 根据命名空间从SchemaContext中获取QNameModule.
     *
     * @param namespace 命名空间
     * @return QNameModule
     */
    QNameModule findQNameModuleByNamespace(String namespace);

    /**
     * 从XmlElement中获取Qname.
     *
     * @param xmlElement xmlElement
     * @return QName
     * @throws DocumentedException if operation fails
     */
    default QName findQName(XmlElement xmlElement) throws DocumentedException {
        String namespace = xmlElement.getNamespace();
        QNameModule nameModule = findQNameModuleByNamespace(namespace);

        return QName.create(nameModule, xmlElement.getName());
    }

    SchemaTreeInference getSchemaNodeFromNamespace(XmlElement xmlElement) throws DocumentedException;

    void parseIntoNormalizedNode(SchemaTreeInference inference,
                                 XmlElement element,
                                 NormalizedNodeStreamWriter writer) throws DocumentedException;

    NormalizedNode parseToNormalizedNode(XmlElement element) throws DocumentedException;

    DOMResult writeNormalizedNode(NormalizedNode normalized, PathArgument pathArgument);

    EffectiveModelContext getSchemaContext();

    SchemaSourceProvider<YangTextSchemaSource> getSourceProvider();

    Set<Capability> supportedCapabilities();

    File schemaDirectory();

    SchemaInferenceStack.Inference getRootInference();

    XmlNodeConverter getXmlNodeConverter();

    void processDynamicConfig(Document input);
}
