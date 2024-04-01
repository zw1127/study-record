package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterEntry {

    //设备id(key)
    private String nodeId;

    //Filter组编号
    private Integer wriFilterListId;
    //Filter组类型
    private WriFilterListTypeEnum wriFilterListType;
    //Filter编号
    private Integer wriFilterNum;
    //匹配类型
    //Filter类型 ip(0) udp(1) tcp(2) icmp(3) igmp(4)，mac
    private WriFilterTypeEnum wriFilterType;

    //match fragment => 界面无
    private Boolean wriFilterFragment;

    //xxx-address
    private FilterEntryAddrConfig filterEntryAddrConfig;

    //Vlan、EthType => 界面无
    private FilterEntryVlanEthConfig filterEntryVlanEthConfig;

    //端口匹配规则
    private FilterEntryPortMatchConfig filterEntryPortMatchConfig;

    //字段匹配规则 => 界面无
    private FilterEntryFieldMatchConfig filterEntryFieldMatchConfig;

    //Icmp匹配规则 => 界面无
    private FilterEntryIcmpConfig filterEntryIcmpConfig;

    //过滤规则匹配动作:drop,deny,permit
    private MatchActionEnum matchAction;
}
