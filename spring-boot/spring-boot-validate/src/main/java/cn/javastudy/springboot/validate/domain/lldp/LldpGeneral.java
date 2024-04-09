package cn.javastudy.springboot.validate.domain.lldp;

import lombok.Data;

@Data
public class LldpGeneral {

    //设备id
    private String nodeId;
    //LLDP帧发送间隔
    private Integer lldpTxInterval;
    //LLDP帧发送倍数
    private Integer lldpTxHold;
    //重新初始化时延
    private Integer lldpReinitDelay;
    //LLDP帧发送时延
    private Integer lldpTxDelay;
    //LLDP告警帧发送间隔
    private Integer lldpNotificationInterval;

}
