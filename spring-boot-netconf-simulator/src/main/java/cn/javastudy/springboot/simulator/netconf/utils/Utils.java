package cn.javastudy.springboot.simulator.netconf.utils;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.opendaylight.netconf.api.messages.NetconfHelloMessageAdditionalHeader;
import org.opendaylight.netconf.api.monitoring.NetconfManagementSession;
import org.opendaylight.netconf.auth.AuthProvider;
import org.opendaylight.netconf.impl.NetconfServerSession;
import org.opendaylight.netconf.shaded.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IetfInetUtil;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddressBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public final class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static final AuthProvider DEFAULT_AUTH_PROVIDER = (username, password) -> {
        LOG.info("Auth with username and password: {}", username);
        return true;
    };

    public static final PublickeyAuthenticator DEFAULT_PUBLIC_KEY_AUTHENTICATOR = (username, key, session) -> {
        LOG.info("Auth with public key: {}", key);
        return true;
    };

    private Utils() {
    }

    public static InetSocketAddress getInetAddress(final String bindingAddress, final String portNumber) {
        IpAddress ipAddress = IpAddressBuilder.getDefaultInstance(bindingAddress);
        final InetAddress inetAd = IetfInetUtil.INSTANCE.inetAddressFor(ipAddress);
        return new InetSocketAddress(inetAd, Integer.parseInt(portNumber));
    }

    @SuppressWarnings("IllegalCatch")
    public static NetconfHelloMessageAdditionalHeader resolveHeader(NetconfManagementSession session) {
        try {
            if (!(session instanceof NetconfServerSession)) {
                return null;
            }
            NetconfServerSession serverSession = (NetconfServerSession) session;

            Field field = ReflectionUtils.findField(serverSession.getClass(), "header");
            if (field == null) {
                return null;
            }

            Object header = getValue(field, serverSession);
            if (header instanceof NetconfHelloMessageAdditionalHeader) {
                return (NetconfHelloMessageAdditionalHeader) header;
            }
        } catch (Throwable throwable) {
            LOG.warn("get field failed. serverSession:{}", session, throwable);
        }
        return null;
    }

    private static <T> Object getValue(Field field, T instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("get instance:{} field:{} value failed", instance, field.getName(), e);
            }
            LOG.warn("get instance:{} field:{} value failed, error:{}", instance, field.getName(), e.getMessage());
        }

        return null;
    }
}
