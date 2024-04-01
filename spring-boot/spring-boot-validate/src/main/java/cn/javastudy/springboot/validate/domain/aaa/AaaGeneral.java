package cn.javastudy.springboot.validate.domain.aaa;

import lombok.Data;

@Data
public class AaaGeneral {

    //设备id(key)
    private String nodeId;
    //实时计费上报间隔(s) 4294967290需要用long存
    private Long aaaAccRealtime;
    //RADIUS服务器全局重传间隔
    private Integer aaaRadiusSerRetrans;
    //RADIUS服务器全局最大重传次数
    private Integer aaaRadiusSerMaxRetrans;
    //RADIUS服务器全局失效时间
    private Long aaaRadiusSerDeadtime;
    //TACACS服务器重传超时时间
    private Integer aaaTacacsSerTimeout;
    //TACACS服务器全局失效时间
    private Long aaaTacacsSerDeadtime;

}
