package cn.javastudy.springboot.validate.domain.mac;

import lombok.Data;

@Data
public class MacForwardEntry {

    //设备id(key)
    private String nodeId; //node_id
    //vlan-id
    private Integer vlanId; //vlan_id
    //mac地址
    private String macAddress;  //mac

    //接口名称
    private String ifIndex;  //if_index

    private String ifName;
    //转发表类型 static(1) dynamic(2)
    private ForwardType macForwardEntryType;  //forward_type
    //转发操作类型 forward(1)
    private ForwardOperEnum macForwardOperType;  //forward_oper

}
