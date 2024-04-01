package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterListEntry {

    private String nodeId;
    //Filter组编号
    private Integer wriFilterListIndex;
    //Filter组类型
    private WriFilterListTypeEnum wriFilterListType;
    //Filter组名称
    private String wriFilterListName;
    //引用计数
    private Integer wriReferenceCount;
    //Filter组容量
    private Integer wriFilterListSize;
    //已配filter个数
    private Integer wriFilterListCnt;
}
