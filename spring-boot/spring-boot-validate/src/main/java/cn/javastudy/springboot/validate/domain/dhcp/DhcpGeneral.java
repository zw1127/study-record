package cn.javastudy.springboot.validate.domain.dhcp;

import lombok.Data;

@Data
public class DhcpGeneral {

    //设备id(key)
    private String nodeId;

    //dhcp使能 start(1),stop(0)
    private Boolean dhcpEnable;

    //DHCP服务器检测使能 enable(1),disable(0)
    private Boolean dhcpServerDetect;

    //DHCP中继用户表刷新周期
    private Integer dhcpTrackerInterval;
}
