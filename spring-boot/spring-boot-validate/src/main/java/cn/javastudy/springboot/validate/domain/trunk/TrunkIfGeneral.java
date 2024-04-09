package cn.javastudy.springboot.validate.domain.trunk;

import lombok.Data;

@Data
public class TrunkIfGeneral {

    //设备id(key)
    private String nodeId;

    private Integer lacpSystemPriority;

}
