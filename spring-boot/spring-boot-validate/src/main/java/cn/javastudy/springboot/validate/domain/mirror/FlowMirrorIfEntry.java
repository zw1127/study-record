package cn.javastudy.springboot.validate.domain.mirror;

import lombok.Data;

@Data
public class FlowMirrorIfEntry {

    //设备id
    private String nodeId;
    //端口索引
    private String ifIndex;
    //以太网名称
    private String ethIfName;
    //Ingress mirror group
    private Integer ingressGroup;
    // Egress mirror group
    private Integer egressGroup;

}
