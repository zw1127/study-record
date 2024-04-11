package cn.javastudy.springboot.validate.domain.vlan;

import lombok.Data;

@Data
public class VlanEthBinding {

    //设备id(key)
    private String nodeId;

    private String ethIfIndex;

    private TagTypeEnum tagType;

    // 绑定的vlan范围，取值范围是：1,4,6,8-11这种格式
    private String vlanString;
}
