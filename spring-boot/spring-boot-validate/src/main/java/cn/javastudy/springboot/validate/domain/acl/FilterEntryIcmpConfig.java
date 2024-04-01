package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterEntryIcmpConfig {
    //icmp配置开关
    private Boolean icmpConfig;
    //icmpTypeMatchAny
    private Boolean icmpTypeMatchAny;
    //icmpCodeMatchAny
    private Boolean icmpCodeMatchAny;
    //icmpType
    private Integer icmpType;
    //icmpCode
    private Integer icmpCode;
}
