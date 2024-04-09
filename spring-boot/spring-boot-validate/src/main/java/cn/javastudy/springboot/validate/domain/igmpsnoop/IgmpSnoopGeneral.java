package cn.javastudy.springboot.validate.domain.igmpsnoop;

import lombok.Data;

@Data
public class IgmpSnoopGeneral {

    //设备id(key)
    private String nodeId;

    //使能igmp
    private Boolean igmpSnoopEnable;

    //IGMP健壮系数
    private Integer igmpRobustCoefficient;

    //配置当前动态路由器接口老化时间
    private Integer igmpV2RouterAging;

}
