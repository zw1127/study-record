package cn.javastudy.springboot.validate.domain.dhcp;

import lombok.Data;

@Data
public class DhcpIfEntry {

    //设备id(key)
    private String nodeId;
    //接口索引
    private String dhcpIfUnit;
    //以太网端口名称
    private String ethIfName;
    //dhcp模式
    private String dhcpIfMode;
    //option82使能
    private Boolean dhcpIfOption82Status;

    //option82动作:drop/keep/replace
    private String dhcpIfOption82Action;
    //option82 circuit-id
    //ascii | format | hex | ip-address
    private String dhcpIfCircuitID;
    //option82 remote-id
    //枚举 format |  ip-address，或者string
    private String dhcpIfRemoteID;

    private Boolean dhcpRelayAddressCheck;
    //配置使能arp学习触发器
    private Boolean dhcpIfArpLearning;
}
