package cn.javastudy.springboot.validate.domain.ethif;

import lombok.Data;

@Data
public class EthIfStormSuppress {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String stormSuppressIfIndex;

    //接口名称
    private String ethifName;

    private Integer stormSuppressEthifNum;

    //阈值类型
    private SuppressIfLevelEnum stormSuppressIfLevel;
    //检测时间间隔
    private Integer stormSuppressIfInterval;
    //风暴抑制动作none(0), block(1),shutdown(2)
    private SuppressIfActionEnum stormSuppressIfAction;
    //自动恢复时间间隔
    private Integer stormSuppressRecoveryInterval;

    //广播风暴控制 enable(1), disable(2)
    private Boolean stormSuppressIfMulticastEnable;
    //组播风暴控制 enable(1), disable(2)
    private Boolean stormSuppressIfBroadcastEnable;
    //dlf风暴控制 enable(1), disable(2)
    private Boolean stormSuppressIfDlfEnable;

    private SuppressIfLevelEnum multicastLevel;

    private SuppressIfLevelEnum broadcastLevel;

    private SuppressIfLevelEnum dlfLevel;

    private Integer stormSuppressIfMulticastMinLimit;

    private Integer stormSuppressIfMulticastMaxLimit;

    private Integer stormSuppressIfBroadcastMinLimit;

    private Integer stormSuppressIfBroadcastMaxLimit;

    private Integer stormSuppressIfDlfMinLimit;

    private Integer stormSuppressIfDlfMaxLimit;
}
