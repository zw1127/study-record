package cn.javastudy.springboot.validate.domain.arp;

import lombok.Data;

@Data
public class ArpTrapGeneral {

    private String nodeId;

    //防攻击-ARP源IP使能
    private Boolean arpSIpTrap;
    //防攻击-ARP源MAC使能
    private Boolean arpSMacTrap;
    //防攻击-防ARP欺骗使能
    private Boolean arpDeceiveTrap;
    //防攻击-免费arp使能
    private Boolean gratuitousArpTrap;

}
