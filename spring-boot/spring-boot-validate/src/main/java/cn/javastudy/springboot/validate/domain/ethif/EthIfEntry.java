package cn.javastudy.springboot.validate.domain.ethif;

import lombok.Data;

@Data
public class EthIfEntry {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String ethifIndex;

    //接口名称
    private String ethifName;

    // 端口描述
    private String ethifDescr;
    // 管理状态
    private EthIfStatusEnum ethifAdminStatus;
    //MDI
    private MdIENUM ifMdi;
    //mtu
    private Integer ifMtu;
    // 端口优先级
    private Integer ethifPriority;
    // （使能）流量控制
    private Boolean ethifFlowCtrlEnable;
    //流量控制自协商状态
    private Boolean ifFlowCtrlAutoNegotiation;

    private Boolean linkDownStatus = false;

    private Integer ethifNum;
}
