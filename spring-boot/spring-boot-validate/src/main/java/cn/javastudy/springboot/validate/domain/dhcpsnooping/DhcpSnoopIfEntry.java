package cn.javastudy.springboot.validate.domain.dhcpsnooping;

import lombok.Data;

@Data
public class DhcpSnoopIfEntry {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String dhcpSnoopIfIndex;
    //以太网名称
    private String ethIfName;
    //dhcpsnoop版本
    private DhcpSnoopIfVersionEnum dhcpSnoopIfVersion;
    //信任接口
    private Boolean dhcpSnoopIfTrust;
    //选项82策略
    private DhcpSnoopIfOption82StrategyEnum dhcpSnoopIfOption82Strategy;
    //选项82使能状态
    private Boolean dhcpSnoopIfOption82Status;
    //选项82的Circuitid
    private String dhcpSnoopIfOption82Circuitid;
    //选项82的Remoteid
    private String dhcpSnoopIfOption82Remoteid;
    //选项82子选项9的内容
    private String dhcpSnoopIfOption82Sub9;
    //mac检查使能状态
    private Boolean dhcpSnoopIfMacAddressCheck;
}
