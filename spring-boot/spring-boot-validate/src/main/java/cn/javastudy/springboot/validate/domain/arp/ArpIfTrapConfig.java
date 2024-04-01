package cn.javastudy.springboot.validate.domain.arp;

import lombok.Data;

@Data
public class ArpIfTrapConfig {

    private Long id;
    //设备id(key)
    private String nodeId;
    //接口索引
    private String ifIndex;

    private String ethifName;

    private Integer ethifNum;

    //报文检查类型使能-ip
    private Boolean ipCheckEnable;
    //报文检查类型使能-mac
    private Boolean macCheckEnable;
    //报文检查类型使能-vlan
    private Boolean vlanCheckEnable;
    //报文检查类型使能-if
    private Boolean ifCheckEnable;
}
