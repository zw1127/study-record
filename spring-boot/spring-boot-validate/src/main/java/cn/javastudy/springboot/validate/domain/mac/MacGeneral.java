package cn.javastudy.springboot.validate.domain.mac;

import lombok.Data;

@Data
public class MacGeneral {

    //设备id(key)
    private String nodeId;

    private Integer macAgeTime;

}
