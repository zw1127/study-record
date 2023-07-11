package cn.javastudy.springboot.simulator.netconf.utils;

import com.google.common.io.Resources;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class XmlUtils {

    private static final Templates PRETTY_PRINT_TEMPLATE;

    static {
        try {
            PRETTY_PRINT_TEMPLATE = TransformerFactory.newInstance()
                .newTemplates(new StreamSource(
                    Resources.getResource(XmlUtil.class, "/pretty-print.xsl").openStream()));
        } catch (IOException | TransformerConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private XmlUtils() {
    }

    public static String toString(final Document document) {
        return toString(document.getDocumentElement());
    }

    public static String toString(final Element xml) {
        return toString(xml, false);
    }

    public static String toString(final XmlElement xmlElement) {
        return toString(xmlElement.getDomElement(), false);
    }

    public static String toString(final Element xml, final boolean addXmlDeclaration) {
        final StringWriter writer = new StringWriter();
        try {
            Transformer transformer = newIndentingTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, addXmlDeclaration ? "no" : "yes");
            transformer.transform(new DOMSource(xml), new StreamResult(writer));
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new IllegalStateException("Unable to serialize xml element " + xml, e);
        }

        return writer.toString();
    }

    /**
     * Return a new {@link Transformer} which performs indentation.
     *
     * @return A new Transformer
     * @throws TransformerConfigurationException if a Transformer can not be created
     */
    public static Transformer newIndentingTransformer() throws TransformerConfigurationException {
        final Transformer ret = PRETTY_PRINT_TEMPLATE.newTransformer();
        ret.setOutputProperty(OutputKeys.INDENT, "yes");
        return ret;
    }

    public static String toString(final Document doc, final boolean addXmlDeclaration) {
        return toString(doc.getDocumentElement(), addXmlDeclaration);
    }

}
