package cn.javastudy.springboot.simulator.netconf.service.impl;

import static cn.javastudy.springboot.simulator.netconf.utils.Constants.MANAGER_IP_KEY;
import static cn.javastudy.springboot.simulator.netconf.utils.Constants.RESOURCE_TEMPLATE_PATH;
import static cn.javastudy.springboot.simulator.netconf.utils.Constants.SIMULATED_CONFIG;
import static cn.javastudy.springboot.simulator.netconf.utils.Constants.SIMULATE_HOME;
import static cn.javastudy.springboot.simulator.netconf.utils.Constants.UUID_KEY;

import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfig;
import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfigKey;
import cn.javastudy.springboot.simulator.netconf.datastore.mapper.SimulatorConfigMapper;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceUniqueInfo;
import cn.javastudy.springboot.simulator.netconf.service.SimulateConfigService;
import cn.javastudy.springboot.simulator.netconf.utils.XmlUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class SimulateConfigServiceImpl implements SimulateConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(SimulateConfigServiceImpl.class);

    private static final StringBuilder TEMPLATE_BUILDER = new StringBuilder();

    @Resource
    private SimulatorConfigMapper simulatorConfigMapper;

    @PostConstruct
    public void init() {
        try {
            String homePath = System.getProperty(SIMULATE_HOME);
            Path templatePath;
            if (StringUtils.isNotEmpty(homePath)) {
                templatePath = Paths.get(homePath, RESOURCE_TEMPLATE_PATH, SIMULATED_CONFIG);
            } else {
                templatePath =
                    ResourceUtils.getFile("classpath:" + RESOURCE_TEMPLATE_PATH + "/" + SIMULATED_CONFIG).toPath();
            }

            String templateString = Files.readString(templatePath, StandardCharsets.UTF_8);
            TEMPLATE_BUILDER.append(templateString);
        } catch (IOException e) {
            LOG.warn("read template file error.", e);
        }
    }

    @Override
    public String initialConfigXml(DeviceUniqueInfo uniqueInfo) {
        String template = TEMPLATE_BUILDER.toString();
        return StringUtils.replaceEach(template,
            new String[]{UUID_KEY, MANAGER_IP_KEY},
            new String[]{uniqueInfo.getUniqueKey(), uniqueInfo.getManageIp()});
    }

    @Override
    public void saveToDb(String deviceId, XmlElement xmlElement) {
        SimulatorConfigKey key = new SimulatorConfigKey(deviceId, xmlElement.getName());

        SimulatorConfig config = new SimulatorConfig();
        config.setDeviceId(deviceId);
        config.setNodeName(xmlElement.getName());
        config.setNodeValue(XmlUtils.toString(xmlElement));

        SimulatorConfig dbConfig = simulatorConfigMapper.selectByKey(key);
        if (dbConfig == null) {
            boolean result = simulatorConfigMapper.insert(config) > 0;
            LOG.debug("insert device:{} config:{} to database result:{}.", deviceId, config.getNodeName(), result);
        } else {
            boolean result = simulatorConfigMapper.updateValueByKey(config) > 0;
            LOG.debug("update device:{} config:{} to database result:{}.", deviceId, config.getNodeName(), result);
        }
    }

    @Override
    public void deleteFromDb(String deviceId, XmlElement beforeElement) {
        SimulatorConfigKey key = new SimulatorConfigKey(deviceId, beforeElement.getName());
        int deleted = simulatorConfigMapper.deleteByKey(key);
        LOG.debug("delete device:{} nodeName:{} deleted size:{}", deleted, beforeElement.getName(), deleted);
    }

}
