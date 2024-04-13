package cn.javastudy.springboot.validate.domain.trunk;

import lombok.Data;

@Data
public class EthTrunkIfEntry {

    //设备id(key)
    private String nodeId;
    //trunk-id
    private Integer ethTrunkId;

    private String ethTrunkIndex;

    //聚合模式 manual(0),staticlacp(1),dynamiclacp(2)
    private TrunkIfMode ethTrunkIfMode;
    //活动接口下限阈值
    private Integer ethTrunkIfMinActiveLinkNum;
    //活动接口上线阈值
    private Integer ethTrunkIfMaxActiveLinkNum;
    //流量分配算法 srcmac(0),destmac(1),srcanddestmac(2),srcip(3),destip(4),srcanddestip(5)
    private TrunkGlobalModeEnum ethTrunkGlobalMode;
    //静态LACP模式-系统优先级
    private Integer lacpSystemPriority;
    //静态LACP模式-LACP超时时间
    private Integer lacpAggTimeout;

}
