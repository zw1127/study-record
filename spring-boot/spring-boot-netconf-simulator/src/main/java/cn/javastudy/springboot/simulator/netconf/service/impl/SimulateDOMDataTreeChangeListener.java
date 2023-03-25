package cn.javastudy.springboot.simulator.netconf.service.impl;

import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfig;
import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfigKey;
import cn.javastudy.springboot.simulator.netconf.datastore.mapper.SimulatorConfigMapper;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceUniqueInfo;
import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import io.lighty.codecs.util.XmlNodeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.dom.api.DOMDataTreeChangeListener;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.DataContainerChild;
import org.opendaylight.yangtools.yang.data.tree.api.DataTreeCandidate;
import org.opendaylight.yangtools.yang.data.tree.api.ModificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class SimulateDOMDataTreeChangeListener implements DOMDataTreeChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(SimulateDOMDataTreeChangeListener.class);

    private final DeviceUniqueInfo uniqueInfo;
    private final XmlNodeConverter xmlNodeConverter;
    private final SimulatorConfigMapper simulatorConfigMapper;
    private final AtomicBoolean initFlag;

    public SimulateDOMDataTreeChangeListener(final DeviceUniqueInfo uniqueInfo,
                                             final AtomicBoolean initFlag,
                                             final SchemaContextService schemaContextService,
                                             final SimulatorConfigMapper simulatorConfigMapper) {
        this.uniqueInfo = uniqueInfo;
        this.xmlNodeConverter = schemaContextService.getXmlNodeConverter();
        this.simulatorConfigMapper = simulatorConfigMapper;
        this.initFlag = initFlag;
    }

    @Override
    public void onDataTreeChanged(@NonNull List<DataTreeCandidate> changes) {
        try {
            if (initFlag.get()) {
                LOG.info("device is init, don't care datachange event.");
                return;
            }

            for (DataTreeCandidate change : changes) {
                ModificationType modificationType = change.getRootNode().getModificationType();
                ContainerNode before = change.getRootNode().getDataBefore()
                    .filter(ContainerNode.class::isInstance)
                    .map(ContainerNode.class::cast)
                    .orElse(null);
                ContainerNode after = change.getRootNode().getDataAfter()
                    .filter(ContainerNode.class::isInstance)
                    .map(ContainerNode.class::cast)
                    .orElse(null);

                // 由于数据都在 (urn:ietf:params:xml:ns:netconf:base:1.0)data 这个命名空间下，因此需要遍历操作
                processDataChange(before, after);
            }

        } catch (Throwable throwable) {
            LOG.warn("onDataTreeChanged error. DeviceUniqueInfo:{}", uniqueInfo, throwable);
        }
    }

    private void processDataChange(ContainerNode before, ContainerNode after) {
        if (before == null && after == null) {
            LOG.warn("node:{} before and after date are null.", uniqueInfo);
            return;
        }
        try {
            List<DataContainerChild> deleteDataList = new ArrayList<>();
            List<DataContainerChild> updateDataList = List.copyOf(after.body());
            if (before != null) {
                deleteDataList.addAll(List.copyOf(before.body()));
                deleteDataList.removeAll(List.copyOf(after.body()));
            }

            if (!CollectionUtils.isEmpty(deleteDataList)) {
                for (DataContainerChild child : deleteDataList) {
                    XmlElement element = XmlElement.fromString(xmlNodeConverter.serializeData(child).toString());
                    deleteFromDb(uniqueInfo.getUniqueKey(), element);
                }
            }

            if (!CollectionUtils.isEmpty(updateDataList)) {
                for (DataContainerChild child : updateDataList) {
                    XmlElement element = XmlElement.fromString(xmlNodeConverter.serializeData(child).toString());
                    saveToDb(uniqueInfo.getUniqueKey(), element);
                }
            }
        } catch (Throwable throwable) {
            LOG.warn("process device:{} DataChange error:", uniqueInfo, throwable);
        }
    }

    private void saveToDb(String deviceId, XmlElement xmlElement) {
        SimulatorConfigKey key = new SimulatorConfigKey(deviceId, xmlElement.getName());

        SimulatorConfig config = new SimulatorConfig();
        config.setDeviceId(deviceId);
        config.setNodeName(xmlElement.getName());
        config.setNodeValue(XmlUtil.toString(xmlElement));

        SimulatorConfig dbConfig = simulatorConfigMapper.selectByKey(key);
        if (dbConfig == null) {
            boolean result = simulatorConfigMapper.insert(config) > 0;
            LOG.debug("insert device:{} config:{} to database result:{}.", deviceId, config.getNodeName(), result);
        } else {
            boolean result = simulatorConfigMapper.updateValueByKey(config) > 0;
            LOG.debug("update device:{} config:{} to database result:{}.", deviceId, config.getNodeName(), result);
        }
    }

    private void deleteFromDb(String deviceId, XmlElement beforeElement) {
        SimulatorConfigKey key = new SimulatorConfigKey(deviceId, beforeElement.getName());
        int deleted = simulatorConfigMapper.deleteByKey(key);
        LOG.debug("delete device:{} nodeName:{} deleted size:{}", deleted, beforeElement.getName(), deleted);
    }


    @Override
    public void onInitialData() {

    }
}
