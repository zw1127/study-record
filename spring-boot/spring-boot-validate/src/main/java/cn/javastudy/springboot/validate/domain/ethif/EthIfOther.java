package cn.javastudy.springboot.validate.domain.ethif;

import lombok.Data;

@Data
public class EthIfOther {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String ethifIndex;

    //接口名称
    private String ethifName;

    private Boolean linkFlapProtectionEnable;

    private Integer linkFlapProtectionThreshold;

    private Integer linkFlapProtectionInterval;

    private Integer linkFlowStatInterval;

}
