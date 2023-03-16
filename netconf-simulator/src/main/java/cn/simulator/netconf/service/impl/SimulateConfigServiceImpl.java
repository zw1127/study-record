package cn.simulator.netconf.service.impl;

import static cn.simulator.netconf.utils.Constants.MANAGER_IP_KEY;
import static cn.simulator.netconf.utils.Constants.RESOURCE_TEMPLATE_PATH;
import static cn.simulator.netconf.utils.Constants.SIMULATED_CONFIG;
import static cn.simulator.netconf.utils.Constants.SIMULATE_HOME;
import static cn.simulator.netconf.utils.Constants.UUID_KEY;

import cn.simulator.netconf.service.SimulateConfigService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.opendaylight.netconf.api.NetconfServerDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimulateConfigServiceImpl implements SimulateConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(SimulateConfigServiceImpl.class);

    private static final StringBuilder TEMPLATE_BUILDER = new StringBuilder();

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
    public String initialConfigXml(String uniqueSign, String manageIp) {
        String template = TEMPLATE_BUILDER.toString();
        return StringUtils.replaceEach(template,
            new String[]{UUID_KEY, MANAGER_IP_KEY},
            new String[]{uniqueSign, manageIp});
    }

    @Override
    public NetconfServerDispatcher getDispatcher() {
        return null;
    }
}
