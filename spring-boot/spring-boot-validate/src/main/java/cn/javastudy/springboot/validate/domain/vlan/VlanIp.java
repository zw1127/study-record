package cn.javastudy.springboot.validate.domain.vlan;

import lombok.Data;

@Data
public class VlanIp {

    //设备id(key)
    private String nodeId;

    //vlan值
    private Integer vlanId;

    // ipv4 or ipv6
    private String ipType;

    // ipv4 or ipv6 address
    private String ip;

    // ipv4 掩码长度 0-32, ipv6 掩码长度：0-128
    private Integer mask;

    private Boolean isSub;

}
