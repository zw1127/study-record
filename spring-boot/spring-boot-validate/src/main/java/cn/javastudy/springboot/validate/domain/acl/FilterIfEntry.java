package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterIfEntry {

    //设备id(key)
    private String nodeId;

    //接口索引
    private String ethIfIndex;

    private String ethIfName;

    //Filter组编号
    private String inFilterListId;

    //Filter组编号
    private String outFilterListId;
}
