package cn.javastudy.springboot.validate.domain.vlan;

import cn.javastudy.springboot.validate.domain.trunk.TrunkGlobalModeEnum;
import cn.javastudy.springboot.validate.domain.trunk.TrunkIfMode;
import lombok.Data;

@Data
public class VlanIf {

    //设备id(key)
    private String nodeId;
    //vlan值
    private Integer vlanId;

    private String vlanDescr;

    //vlan接口未知多播处理
    private UnknownMulticastActionEnum portBasedVlanUnknownMulticastDiscard;
    //vlan接口管理状态（可配置）
    private Boolean adminStatus;

}
