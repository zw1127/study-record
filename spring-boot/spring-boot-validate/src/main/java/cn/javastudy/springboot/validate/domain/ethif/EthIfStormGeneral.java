package cn.javastudy.springboot.validate.domain.ethif;

import lombok.Data;

@Data
public class EthIfStormGeneral {

    //设备id(key)
    private String nodeId;

    //自动恢复时间间隔
    private Integer stormSuppressRecoveryInterval;
}
