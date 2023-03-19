package cn.javastudy.springboot.simulator.netconf.service;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceUniqueInfo;

public interface SimulateConfigService {

    /**
     * 生成指定唯一标识和ManageIP的初始化xml.
     *
     * @param uniqueInfo 模拟器唯一信息对象
     * @return 设备初始化xml
     */
    String initialConfigXml(DeviceUniqueInfo uniqueInfo);

}
