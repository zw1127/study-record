package cn.javastudy.springboot.validate.domain.acl;

import lombok.Data;

@Data
public class FilterEntryAddrConfig {

    //src any
    private Boolean srcMatchAny;
    //dst any
    private Boolean dstMatchAny;
    //src-ip4/6、src-mac地址
    private String srcAddr;
    //src-ip4/6、src-mac掩码
    private Integer srcPrefixLength;
    //dst-ip4/6、dst-mac地址
    private String dstAddr;
    //dst-ip4/6、dst-mac掩码
    private Integer dstPrefixLength;
}
