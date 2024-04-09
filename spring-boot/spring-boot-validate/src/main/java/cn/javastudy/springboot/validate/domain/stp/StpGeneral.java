package cn.javastudy.springboot.validate.domain.stp;

import lombok.Data;

@Data
public class StpGeneral {

    //设备Id
    private String nodeId;
    //STP模式
    private StpModeEnum stpModel;
    //修订版本
    private Integer reviseEdition;
    //最大老化时间
    private Integer maxAge;
    //hello报文间隔
    private Integer helloTimeInterval;
    //路径开销标准
    private CostStandardEnum costStandard;
    //tc-bpdu保护
    private Boolean tcbpduEnable;
    //域名
    private String domainName;
    //最大跳数
    private Integer maxHop;
    //转发时延
    private Integer forwardDelay;
    //hello周期发包次数
    private Integer helloTimeNum;
    //bpdu保护
    private Boolean bpduEnable;
    //所有端口为边缘端口
    private Boolean edgePortEnable;

}
