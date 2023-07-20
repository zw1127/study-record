package cn.javastudy.springboot.simulator.netconf.utils;

import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

final class HardcodedNamespaceResolver implements NamespaceContext {
    private final Map<String, String> prefixesToNamespaces;

    HardcodedNamespaceResolver(final String prefix, final String namespace) {
        prefixesToNamespaces = Map.of(prefix, namespace);
    }

    /**
     * Returns the URI for all prefixes needed. Wherever possible it
     * uses {@code XMLConstants}.
     *
     * @param prefix    prefix
     * @return uri      uniform resource identifier
     */
    @Override
    public String getNamespaceURI(final String prefix) {
        final var namespace = prefixesToNamespaces.get(prefix);
        if (namespace == null) {
            throw new IllegalStateException("Prefix mapping not found for " + prefix);
        }
        return namespace;
    }

    @Override
    public String getPrefix(final String namespaceURI) {
        // Not needed in this context.
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(final String namespaceURI) {
        // Not needed in this context.
        return null;
    }
}
