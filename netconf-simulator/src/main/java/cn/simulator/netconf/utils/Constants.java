package cn.simulator.netconf.utils;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.extension.rev131210.$YangModuleInfoImpl;
import org.opendaylight.yangtools.yang.binding.YangModuleInfo;

public interface Constants {

    String SIMULATE_HOME = "simulate.home";

    String YANG_SCHEMAS_PATH = "yang-schemas";

    //配置文件自定义模板路径默认值
    String RESOURCE_TEMPLATE_PATH = "resources-template";

    String SIMULATED_CONFIG = "simulated-config.xml";

    String UUID_KEY = "{UUID}";
    String MANAGER_IP_KEY = "{MANAGER_IP}";

    Set<String> DEFAULT_BASE_CAPABILITIES_WITHOUT_EXI = ImmutableSet.of(
        XmlNetconfConstants.URN_IETF_PARAMS_NETCONF_BASE_1_0,
        XmlNetconfConstants.URN_IETF_PARAMS_NETCONF_BASE_1_1,
        XmlNetconfConstants.URN_IETF_PARAMS_NETCONF_CAPABILITY_NOTIFICATION_1_0
    );

    Set<YangModuleInfo> DEFAULT_SUPPORTED_MODULE_INFOS = ImmutableSet.of(
        $YangModuleInfoImpl.getInstance()
    );

}
