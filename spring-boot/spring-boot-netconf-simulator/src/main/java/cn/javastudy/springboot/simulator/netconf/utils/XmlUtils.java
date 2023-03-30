package cn.javastudy.springboot.simulator.netconf.utils;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class XmlUtils {

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    private static final String PRETTY_PRINT_XSL =
        "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
            + "    <xsl:strip-space elements=\"*\"/>"
            + "    <xsl:output method=\"xml\" encoding=\"UTF-8\"/>"
            + "    <xsl:template match=\"@*|node()\">"
            + "        <xsl:copy>"
            + "            <xsl:apply-templates select=\"@*|node()\"/>"
            + "        </xsl:copy>"
            + "    </xsl:template>"
            + "</xsl:stylesheet>";

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
        try {
            StreamSource streamSource = new StreamSource(new StringReader(PRETTY_PRINT_XSL));
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer(streamSource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, addXmlDeclaration ? "no" : "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);

            return result.getWriter().toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new IllegalStateException("Unable to serialize xml element " + xml, e);
        }
    }

    public static String toString(final Document doc, final boolean addXmlDeclaration) {
        return toString(doc.getDocumentElement(), addXmlDeclaration);
    }

}
