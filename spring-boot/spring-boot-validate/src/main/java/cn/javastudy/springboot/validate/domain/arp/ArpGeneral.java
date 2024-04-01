package cn.javastudy.springboot.validate.domain.arp;

import lombok.Data;

@Data
public class ArpGeneral {

    private String nodeId;

    //ARP严格学习使能
    private Boolean arpLearingStrict;
    //动态ARP老化时间
    private Integer arpAgingTime;
    //ARP探测间隔
    private Integer arpDetectTime;

}
