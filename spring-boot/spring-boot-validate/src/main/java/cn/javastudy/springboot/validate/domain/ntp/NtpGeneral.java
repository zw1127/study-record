package cn.javastudy.springboot.validate.domain.ntp;

import lombok.Data;

@Data
public class NtpGeneral {

    private String nodeId;
    //NTP客户端更新间隔
    private Integer clientUpdateInterval;
    //客户端更新间隔幂数值 老网管4-17？
    private Integer updatePowerValue;
    //NTP服务端广播间隔
    private Integer serverBroadcastInterval;
    //服务端广播间隔幂数值  老网管4-17？
    private Integer broadcastPowerValue;
    //NTP层级 老网管1-16？
    private Integer stratum;
    //NTP主时钟
    private Boolean primaryClock;
    //NTP认证
    private Boolean authEnable;

}
