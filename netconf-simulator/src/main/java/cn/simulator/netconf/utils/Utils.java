package cn.simulator.netconf.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.opendaylight.netconf.auth.AuthProvider;
import org.opendaylight.netconf.shaded.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IetfInetUtil;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddressBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void sleep(int timeSeconds) {
        try {
            TimeUnit.SECONDS.sleep(timeSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
