package cn.javastudy.springboot.validate.domain.dhcpsnooping;

import lombok.Data;

@Data
public class DhcpSnoopingGeneral {

    //设备id(key)
    private String nodeId;
    //dhcpsnoop全局状态
    private Boolean dhcpSnoopStatus;
    //dhcp server探测功能使能状态
    private Boolean dhcpSnoopServerDetect;
    //dhcpsnoop绑定用户数
    private Integer dhcpSnoopUserNumber;
}
