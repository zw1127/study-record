package cn.javastudy.springboot.validate.domain.aaa;

import lombok.Data;

@Data
public class AaaTacacsServer {

    //设备id(key)
    private String nodeId;
    //服务器名称
    private String aaaServerName;
    //服务器Ip类型
    private IpAddressType aaaServerType;
    //服务器Ip地址
    private String aaaServerIp;
    //密钥
    private String aaaServerKey;
}
