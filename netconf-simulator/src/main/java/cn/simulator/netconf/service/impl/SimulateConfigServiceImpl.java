package cn.simulator.netconf.service.impl;

import static cn.simulator.netconf.utils.Constants.MANAGER_IP_KEY;
import static cn.simulator.netconf.utils.Constants.RESOURCE_TEMPLATE_PATH;
import static cn.simulator.netconf.utils.Constants.SIMULATED_CONFIG;
import static cn.simulator.netconf.utils.Constants.SIMULATE_HOME;
import static cn.simulator.netconf.utils.Constants.UUID_KEY;

import cn.simulator.netconf.datastore.mapper.SimulatorConfigMapper;
import cn.simulator.netconf.domain.DeviceUniqueInfo;
import cn.simulator.netconf.properties.NetconfSimulatorProperties;
import cn.simulator.netconf.rpchandler.RpcHandler;
import cn.simulator.netconf.rpchandler.RpcHandlerDefault;
import cn.simulator.netconf.service.SchemaContextService;
import cn.simulator.netconf.service.SimulateConfigService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimulateConfigServiceImpl implements SimulateConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(SimulateConfigServiceImpl.class);

    private static final StringBuilder TEMPLATE_BUILDER = new StringBuilder();

    private static final RpcHandler RPC_HANDLER = new RpcHandlerDefault();
    private final Map<DeviceUniqueInfo, DOMDataBroker> deviceDataBrokerMap = new ConcurrentHashMap<>();
    @Resource
    private SchemaContextService schemaContextService;

    @Resource
    private NetconfSimulatorProperties properties;

    @Resource
    private SimulatorConfigMapper simulatorConfigMapper;

    @PostConstruct
    public void init() {
        String homePath = System.getProperty(SIMULATE_HOME, ".");
        Path templatePath = Paths.get(homePath, RESOURCE_TEMPLATE_PATH, SIMULATED_CONFIG);

        try {
            String templateString = Files.readString(templatePath, StandardCharsets.UTF_8);
            TEMPLATE_BUILDER.append(templateString);
        } catch (IOException e) {
            LOG.warn("read template file {} error.", templatePath);
        }
    }

    @Override
    public String initialConfigXml(DeviceUniqueInfo uniqueInfo) {
        String template = TEMPLATE_BUILDER.toString();
        return StringUtils.replaceEach(template,
            new String[]{UUID_KEY, MANAGER_IP_KEY},
            new String[]{uniqueInfo.getUniqueKey(), uniqueInfo.getManageIp()});
    }

}
