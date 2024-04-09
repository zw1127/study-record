package cn.javastudy.springboot.validate.domain.stp;

import lombok.Data;

@Data
public class StpIfEntry {

    //设备id(key)
    private String nodeId;
    //接口索引
    private String ifIndex;
    //以太网端口名称
    private String ethIfName;
    //管理路径开销
    private Integer managePathCost;
    //边缘端口
    private Boolean stpEdgePortEnable;
    //点到点管理
    private PtpManageEnum ptpManageEnable;
    //Bpdu-guard-forward
    private Boolean bpduGuardEnable;
    //根保护使能
    private Boolean stpRootGuardEnable;

}
