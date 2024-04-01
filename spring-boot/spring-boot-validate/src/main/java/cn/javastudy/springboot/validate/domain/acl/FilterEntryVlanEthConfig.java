package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterEntryVlanEthConfig {

    //match vlan or eth-type,使能
    private Boolean matchVlanOrEth;
    //match vlan匹配使能
    private Boolean matchVlan;
    //outerVlan使能
    private Boolean outerVlan;
    //outer vlan是否匹配所有
    private Boolean outerVlanMatchAny;
    //outer Vlan低范围
    private Integer outerVlanRangeFrom;
    //outer Vlan高范围
    private Integer outerVlanRangeTo;
    //outervlan优先级
    private Integer outerVlanPriority;
    //innerVlan使能
    private Boolean innerVlan;
    //inner vlan是否匹配所有
    private Boolean innerVlanMatchAny;
    //outer Vlan低范围
    private Integer innerVlanRangeFrom;
    //outer Vlan高范围
    private Integer innerVlanRangeTo;
    //innervlan优先级
    private Integer innerVlanPriority;
    //matchEthType匹配使能
    private Boolean matchEthType;
    //match eth的选项:ip、arp、digital_protocol_value
    private EthTypeOptionEnum ethTypeOption;
    //match eth的选项参数digital_protocol_value(0x0600-0xfffe),先用字符串存
    private String ethTypeParam;
}
