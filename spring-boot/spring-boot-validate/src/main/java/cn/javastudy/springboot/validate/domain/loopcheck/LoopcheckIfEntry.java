package cn.javastudy.springboot.validate.domain.loopcheck;

import lombok.Data;

@Data
public class LoopcheckIfEntry {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String loopcheckIfIndex;

    private String ethifName;

    private Integer ethifNum;

    private LoopcheckIfAction loopcheckAction;

    //环回检测使能
    private Boolean loopcheckIfEnable;

}
