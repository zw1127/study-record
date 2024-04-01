package cn.javastudy.springboot.validate.domain.arp;

import lombok.Getter;

@Getter
public enum ArpTypeEnum {

    dynamicType(1),
    staticType(2),
    dhcpType(3),
    otherType(4);

    private final int value;

    ArpTypeEnum(int value) {
        this.value = value;
    }
}
