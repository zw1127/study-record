package cn.javastudy.springboot.simulator.netconf.utils;

import static java.util.Objects.requireNonNull;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceBatchBaseInfo;
import cn.javastudy.springboot.simulator.netconf.properties.DynamicConfig;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.opendaylight.netconf.api.messages.NetconfHelloMessageAdditionalHeader;
import org.opendaylight.netconf.api.monitoring.NetconfManagementSession;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.auth.AuthProvider;
import org.opendaylight.netconf.impl.NetconfServerSession;
import org.opendaylight.netconf.shaded.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IetfInetUtil;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddressBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public final class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static final XPathFactory FACTORY = XPathFactory.newInstance();
    private static final NamespaceContext NS_CONTEXT =
        new HardcodedNamespaceResolver("netconf", XmlNetconfConstants.URN_IETF_PARAMS_XML_NS_NETCONF_BASE_1_0);
    private static final String DELIMITER = "/";
    private static final String NAMESPACE_TEMP = "*[local-name()='{TEMP}']";
    private static final String TEMP_VALUE = "{TEMP}";

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

    // 生成指定区间和指定小数位数的随机小数
    public static BigDecimal getRandomDecimal(BigDecimal min, BigDecimal max, int decimalPlaces) {
        BigDecimal range = max.subtract(min);
        BigDecimal randomFactor = BigDecimal.valueOf(Math.random());
        BigDecimal scaledValue = range.multiply(randomFactor);
        BigDecimal roundedValue = scaledValue.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return min.add(roundedValue);
    }

    public static String buildPath(String path) {
        String[] split = StringUtils.split(path, DELIMITER);
        if (split == null || split.length == 0) {
            return path;
        }

        return DELIMITER + Arrays.stream(split)
            .map(subpath -> StringUtils.replace(NAMESPACE_TEMP, TEMP_VALUE, subpath))
            .collect(Collectors.joining(DELIMITER));
    }

    public static NodeList evaluate(Document document, DynamicConfig config) {
        try {
            // 执行 XPath 查询
            String path = config.getPath();
            XPathExpression expression = compileXPath(buildPath(path));
            Object evaluate = expression.evaluate(document, XPathConstants.NODESET);
            if (evaluate instanceof NodeList) {
                return (NodeList) evaluate;
            }
        } catch (XPathExpressionException e) {
            LOG.warn("evaluate error.", e);
        }

        return null;
    }

    private static XPathExpression compileXPath(final String xpath) {
        final XPath newXPath = FACTORY.newXPath();
        newXPath.setNamespaceContext(NS_CONTEXT);
        try {
            return newXPath.compile(xpath);
        } catch (final XPathExpressionException e) {
            throw new IllegalStateException("Error while compiling xpath expression " + xpath, e);
        }
    }

    public static List<String> generateDeviceIdList(DeviceBatchBaseInfo baseInfo) {
        String deviceIdPrefix = requireNonNull(baseInfo.getDeviceIdPrefix(), "deviceIdPrefix is null");
        int deviceIdStart = requireNonNull(baseInfo.getDeviceIdStart(), "deviceIdStart is null");
        int batchSize = requireNonNull(baseInfo.getBatchSize(), "batchSize is null");

        Integer deviceIdLength = Optional.ofNullable(baseInfo.getDeviceIdLength()).orElse(0);

        // 根据 deviceIdStart 和 deviceIdPrefix 生成deviceId列表
        return IntStream.range(deviceIdStart, deviceIdStart + batchSize)
            .mapToObj(i -> Utils.generateDeviceId(deviceIdLength, deviceIdPrefix, i))
            .collect(Collectors.toList());
    }

    public static String generateDeviceId(Integer deviceIdLength, String deviceIdPrefix, int deviceIdNum) {
        int length = deviceIdLength - deviceIdPrefix.length();
        if (length > 0) {
            return deviceIdPrefix + StringUtils.leftPad(Integer.toString(deviceIdNum), length, '0');
        }

        return deviceIdPrefix + deviceIdNum;
    }
}
