package cn.javastudy.springboot.validate.domain.ntp;

import cn.javastudy.springboot.validate.domain.aaa.IpAddressType;
import lombok.Data;

@Data
public class NtpInstanceEntry {

    //设备id
    private String nodeId;
    //实例索引
    private Long instanceIndex;

    //同步模式
    /*
    1：对等体主动模式；
    2：对等体被动模式；
    3：客户端模式；
    4：服务器模式；
    5：广播服务器模式；
    6：广播客户端模式
    7：组播服务器模式；
    8：组播客户端模式
     */
    private Integer syncMode;
    //地址类型
    private IpAddressType addressType;
    //服务器ip
    private String serverIp;
    //服务版本号
    private Integer serviceVersion;
    //认证id号
    private Long authId;
    //端口号
    private Integer portNum;
    //服务层级
    private Integer serverLevel;
    //上一级时差
    private String upperTimeDiff;
    //上一级延时
    private String upperTimeDalay;
    //上一级离差
    private String upperTimeDeviation;
    //VPN实例
    private String vpnInstance;

}
