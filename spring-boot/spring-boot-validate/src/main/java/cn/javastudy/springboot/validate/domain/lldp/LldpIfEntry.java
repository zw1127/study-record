package cn.javastudy.springboot.validate.domain.lldp;

import lombok.Data;

@Data
public class LldpIfEntry {

    //设备id
    private String nodeId;
    //端口索引
    private String lldpIfIndex;
    //以太网名称
    private String ethIfName;
    private String lldpIfIndexName;//无用字段不用管
    //LLDP管理状态：lldpPortConfigAdminStatus
    private LldpCtrlStatusEnum lldpCtrlEnable;
    //LLDP告警控制：lldpPortConfigNotificationEnable
    private Boolean lldpAlarmEnable;
    //PortDescription
    private Boolean lldpTlvPortDescription;
    //SystemDescription
    private Boolean lldpTlvSystemDescription;
    //SystemName
    private Boolean lldpTlvSystemName;
    //SystemCapability
    private Boolean lldpTlvSystemCapability;

}
