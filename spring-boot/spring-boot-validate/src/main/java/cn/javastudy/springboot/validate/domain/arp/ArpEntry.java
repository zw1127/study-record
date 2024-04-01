package cn.javastudy.springboot.validate.domain.arp;

import lombok.Data;

@Data
public class ArpEntry {

    private String nodeId;

    private String desIp;

    private String desMac;

    private ArpTypeEnum arpType;

    private String ifIndex;

}
