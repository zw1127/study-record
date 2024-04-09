package cn.javastudy.springboot.validate.domain.mirror;

import lombok.Data;

@Data
public class MirrorGroupEntry {

    //设备id
    private String nodeId;
    //MIRROR组ID
    private Integer mirrorGroupNum;
    //MIRROR端口，以逗号分割，如：17410,17411,17412
    private String mirrorIfIndex;
    //以太网名称
    private String ethIfName;
    //镜像组类型 当前固定为local
    private String mirrorGroupType;

}
