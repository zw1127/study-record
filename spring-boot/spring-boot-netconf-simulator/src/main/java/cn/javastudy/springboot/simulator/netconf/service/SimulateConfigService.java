package cn.javastudy.springboot.simulator.netconf.service;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceUniqueInfo;
import org.opendaylight.netconf.api.xml.XmlElement;

public interface SimulateConfigService {

    /**
     * 生成指定唯一标识和ManageIP的初始化xml.
     *
     * @param uniqueInfo 模拟器唯一信息对象
     * @return 设备初始化xml
     */
    String initialConfigXml(DeviceUniqueInfo uniqueInfo);

    void saveToDb(String deviceId, XmlElement xmlElement);

    void deleteFromDb(String deviceId, XmlElement beforeElement);
}
