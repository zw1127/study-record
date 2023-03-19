package cn.simulator.netconf.monitoring;

public final class MonitoringConstants {

    private MonitoringConstants() {
        // not called - private constructor for utility class
    }

    public static final String MODULE_NAME = "ietf-netconf-monitoring";
    public static final String MODULE_REVISION = "2010-10-04";

    public static final String NAMESPACE = "urn:ietf:params:xml:ns:yang:" + MODULE_NAME;
    public static final String EXTENSION_NAMESPACE = NAMESPACE + "-extension";

    public static final String EXTENSION_NAMESPACE_PREFIX = "ncme";

    public static final String URI = String.format("%s?module=%s&revision=%s", NAMESPACE, MODULE_NAME, MODULE_REVISION);

    public static final String NETCONF_MONITORING_XML_ROOT_ELEMENT = "netconf-state";
}