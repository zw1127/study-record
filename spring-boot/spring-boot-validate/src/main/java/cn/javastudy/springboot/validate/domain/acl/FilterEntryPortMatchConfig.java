package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterEntryPortMatchConfig {
    //配置端口匹配规则
    private Boolean portMatchConfig;
    //src port any
    private Boolean srcPortMatchAny;
    //dst port any
    private Boolean dstPortMatchAny;
    //src-port
    private Integer srcPort;
    //src-port 上限
    private Integer srcRangePort;
    //dst-port
    private Integer dstPort;
    //dst-port 上限
    private Integer dstRangePort;
    //match xxx 枚举值对应下面的使能: Precedence、IpDscp、IpTtl、ProtoType
    private String portMatchMode;
    //match Precedence参数
    private Integer wriFilterPrecedence;
    //match dscp 参数
    private Integer wriFilterIpDscp;
    //match ttl参数
    private Integer wriFilterIpTtl;
    //match prototype参数
    private Integer wriFilterProtoType;
}
