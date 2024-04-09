package cn.javastudy.springboot.validate.domain.stp;

import lombok.Data;

@Data
public class StpInstanceEntry {

    //设备Id
    private String nodeId;
    //实例id
    private Integer stpInstanceId;
    //实例优先级
    private Integer stpInstancePriority;
    //根节点保护
    private Boolean stpRootGuard;
    //实例应用的vlan列表
    private String stpInstanceVlanList;

}
