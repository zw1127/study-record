package cn.javastudy.springboot.simulator.netconf.ops;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Base64;
import java.util.Optional;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.opendaylight.netconf.util.mapping.AbstractSingletonNetconfOperation;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract class AbstractConfigOperation extends AbstractSingletonNetconfOperation {
    static final String URL_KEY = "url";
    static final String CONFIG_KEY = "config";
    private static final int TIMEOUT_MS = 5000;

    protected AbstractConfigOperation(final String netconfSessionIdForReporting) {
        super(netconfSessionIdForReporting);
    }

    protected static NodeList getElementsByTagName(final XmlElement parent, final String key) throws
        DocumentedException {
        final Element domParent = parent.getDomElement();
        final NodeList elementsByTagName;

        if (Strings.isNullOrEmpty(domParent.getPrefix())) {
            elementsByTagName = domParent.getElementsByTagName(key);
        } else {
            elementsByTagName = domParent.getElementsByTagNameNS(parent.getNamespace(), key);
        }

        return elementsByTagName;
    }

    static XmlElement getConfigElement(final XmlElement parent) throws DocumentedException {
        final Optional<XmlElement> configElement = parent.getOnlyChildElementOptionally(CONFIG_KEY);
        if (configElement.isPresent()) {
            return configElement.get();
        }

        final Optional<XmlElement> urlElement = parent.getOnlyChildElementOptionally(URL_KEY);
        if (urlElement.isEmpty()) {
            throw new DocumentedException("Invalid RPC, neither <config> not <url> element is present",
                ErrorType.PROTOCOL, ErrorTag.MISSING_ELEMENT, ErrorSeverity.ERROR);
        }

        final Document document = getDocumentFromUrl(urlElement.get().getTextContent());
        return XmlElement.fromDomElementWithExpected(document.getDocumentElement(), CONFIG_KEY,
            XmlNetconfConstants.URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0);
    }

    /**
     * Parses XML Document available at given URL.
     *
     * <p>JDK8 supports URL schemes that include http, https, file, and jar, but {@link URLStreamHandler}s for other
     * protocols (e.g. ftp) may be available.
     *
     * @param url URL as defined in RFC 2396
     * @see URL#URL(String, String, int, String)
     */
    private static Document getDocumentFromUrl(final String url) throws DocumentedException {
        try (InputStream input = openConnection(new URL(url))) {
            return XmlUtil.readXmlToDocument(input);
        } catch (MalformedURLException e) {
            throw new DocumentedException(url + " URL is invalid or unsupported", e,
                ErrorType.APPLICATION, ErrorTag.INVALID_VALUE, ErrorSeverity.ERROR);
        } catch (IOException e) {
            throw new DocumentedException("Could not open URL " + url, e,
                ErrorType.APPLICATION, ErrorTag.OPERATION_FAILED, ErrorSeverity.ERROR);
        } catch (SAXException e) {
            throw new DocumentedException("Could not parse XML at" + url, e,
                ErrorType.APPLICATION, ErrorTag.OPERATION_FAILED, ErrorSeverity.ERROR);
        }
    }

    private static InputStream openConnection(final URL url) throws IOException {
        final URLConnection connection = url.openConnection();
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);

        // Support Basic Authentication scheme, e.g. http://admin:admin@localhost:8000/config.conf
        if (url.getUserInfo() != null) {
            String basicAuth = "Basic " + Base64.getUrlEncoder().encodeToString(url.getUserInfo().getBytes(UTF_8));
            connection.setRequestProperty("Authorization", basicAuth);
        }
        return connection.getInputStream();
    }
}
