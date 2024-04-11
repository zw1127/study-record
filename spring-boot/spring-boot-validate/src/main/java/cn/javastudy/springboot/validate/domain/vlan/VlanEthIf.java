package cn.javastudy.springboot.validate.domain.vlan;

import lombok.Data;

@Data
public class VlanEthIf {

    //设备id(key)
    private String nodeId;

    private String ethIfIndex;

    private EthIfTypeEnum ethIfType;

    //端口pvid值
    private Integer ifPvid;

    //qinq使能
    private Boolean ifQinqEnable;

}
