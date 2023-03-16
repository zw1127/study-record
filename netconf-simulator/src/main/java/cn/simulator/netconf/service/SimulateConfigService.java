package cn.simulator.netconf.service;

import org.opendaylight.netconf.api.NetconfServerDispatcher;

public interface SimulateConfigService {

    /**
     * 生成指定唯一标识和ManageIP的初始化xml.
     *
     * @param uniqueSign 唯一标识
     * @param manageIp   管理IP
     * @return 设备初始化xml
     */
    String initialConfigXml(String uniqueSign, String manageIp);

    NetconfServerDispatcher getDispatcher();

}
