package cn.javastudy.springboot.validate.domain.igmpsnoop;

import lombok.Data;

@Data
public class IgmpSnoopVlanEntry {

    //设备id
    private String nodeId;
    //vlan值
    private Integer igmpSnoopVlan;
    //igmp-snooping版本
    private IgmpSnoopVersionEnum igmpSnoopVersion;
    //工作模式
    private IgmpSnoopWorkModeEnum igmpSnoopWorkMode;
    //使能查询器
    private Boolean igmpSnoopQuerierEnable;
    //IP头中必须带有route alert
    private Boolean igmpSnoopRequireRouteAlert;

    private IgmpSnoopForwardModeEnum igmpSnoopMvlanForwardMode;

}
