package cn.javastudy.springboot.validate.domain.trunk;

import lombok.Data;

@Data
public class EthIfTrunkEntry {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String ethifIndex;

    //trunk-id
    private String ethIfTrunkPort;
    //trunk模式使能
    private Boolean ethIfTrunkEnable;

}
