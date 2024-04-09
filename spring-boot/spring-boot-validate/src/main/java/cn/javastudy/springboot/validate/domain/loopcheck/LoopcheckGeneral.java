package cn.javastudy.springboot.validate.domain.loopcheck;

import lombok.Data;

@Data
public class LoopcheckGeneral {

    //设备id(key)
    private String nodeId;

    //环路阻塞关闭动作模式 port-block(1),vlan-block(2)
    private LoopActionEnum loopcheckLoopAction;
    //检测报文的发送间隔
    private Integer loopcheckSendInterval;
    //恢复时间
    private Integer loopcheckRecoverTime;
    //告警使能
    private Boolean loopcheckAlarmEnable;

}
