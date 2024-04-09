package cn.javastudy.springboot.validate.domain.mac;

import cn.javastudy.springboot.validate.domain.loopcheck.LoopActionEnum;
import lombok.Data;

@Data
public class MacFlapping {

    //设备id(key)
    private String nodeId;  //node_id

    //mac漂移使能
    private Boolean flapDetectEnable;  //detect_enable
    //配置地址漂移后动作 quit-vlan/error-down
    private String flappingAction;  //action
    //配置MAC地址漂移表项的老化时间
    private Integer flapAgeTime;  //age_time
    //配置quit-vlan动作后的自动恢复时间
    private Integer flapQuitVlanRecoverTime;  //qvlan_recover_time
    //添加VLAN白名单 exclude-vlan 3,5,200-210
    private String flappingDetectionExcludeVlan;  //exclude_vlan

}
