package cn.javastudy.springboot.validate.domain.ethif;

import lombok.Data;

@Data
public class EthIfEntry {

    //设备id(key)
    private String nodeId;
    //Dot1x使能
    private Boolean wridot1xPaeSystemAuthControl;
    //使能接口AAA认证
    private Boolean wridot1xPaeSystemInterfaceAAAEnable;
    //全局AAA认证方法名
    private String wridot1xPaeSystemAuthName;
    //全局AAA计费方法名
    private String wridot1xPaeSystemAccountName;
}
