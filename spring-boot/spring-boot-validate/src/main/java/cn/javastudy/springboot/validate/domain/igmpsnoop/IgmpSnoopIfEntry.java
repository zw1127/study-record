package cn.javastudy.springboot.validate.domain.igmpsnoop;

import lombok.Data;

@Data
public class IgmpSnoopIfEntry {

    //设备id
    private String nodeId;
    //接口索引
    private String igmpSnoopIfIndex;
    //以太网名称
    private String ethIfName;
    //使能igmp
    private Boolean igmpSnoopEnable;
    //使能igmp控制模式
    private Boolean igmpSnoopCtrlMode;
    //使能fast-leave功能
    private Boolean igmpSnoopFastLeave;

}
