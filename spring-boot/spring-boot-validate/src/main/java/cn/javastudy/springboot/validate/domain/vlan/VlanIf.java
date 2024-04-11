package cn.javastudy.springboot.validate.domain.vlan;

import lombok.Data;

@Data
public class VlanIf {

    //设备id(key)
    private String nodeId;
    //vlan值
    private Integer vlanId;

    private String vlanDescr;

    //vlan接口未知多播处理
    private UnknownMulticastActionEnum portBasedVlanUnknownMulticastDiscard;
    //vlan接口管理状态（可配置）
    private Boolean adminStatus;

//    //主ipv4 地址
//    private String primaryIpv4Address;
//
//    //主ipv4 掩码长度 0-32
//    private String ipv4Mask;
//
//    //vlan接口ipv6使能
//    private Boolean ipv6Enable;
//
//    //主ipv6 地址
//    private String primaryIpv6Address;
//
//    //主ipv6 掩码长度：0-128
//    private String ipv6Mask;
}
