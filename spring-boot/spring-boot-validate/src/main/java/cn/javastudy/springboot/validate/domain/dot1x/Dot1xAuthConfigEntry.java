package cn.javastudy.springboot.validate.domain.dot1x;

import lombok.Data;

@Data
public class Dot1xAuthConfigEntry {

    private String nodeId;
    //接口索引
    private String ifIndex;
    //以太网名称
    private String ethIfName;
    //802.1x状态
    private Boolean wridot1xStatus;
    //配置aaa计费方法
    private String wridot1xAccountName;
    //配置aaa认证方法名
    private String wridot1xAuthName;
    //最大用户数
    private Integer wridot1xMaxSuppNumber;
    //配置重认证周期
    private Integer wridot1xAuthReAuthPeriod;
    //服务器超时时间
    private Integer wridot1xAuthServerTimeout;
    //允许认证最大次数
    private Integer wridot1xAuthMaxReq;
}
