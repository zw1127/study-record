package cn.javastudy.springboot.validate.domain.ethif;

import lombok.Data;

@Data
public class EthIfStatistic {

    //设备id(key)
    private String nodeId;
    //端口索引
    private String ethifIndex;

    //接口名称
    private String ethifName;

    // 自协商 auto negotiate(1), noauto(2)
    private Boolean ethifAutoNego;
    //自协商双工模式 双工模式 half, full,default
    private String autoNegotiateDuplex;
    //自协商速率
    private String autoNegotiateSpeed;

    private String[] autoNegotiateDuplexList;
    private String[] autoNegotiateSpeedList;

    // noauto双工模式 half(0), full(1)
    private DuplexEnum ethifDuplex;
    // noauto端口速率
    private Integer ethifSpeed;

    private String inLevel;//in kbps,percent

    private String outLevel;//out kbps,percent

    //输入带宽控制 enable(1),disable(2)
    private Boolean ethifInLimitCtrl;
    //输入带宽控制粒度
    private Integer ethifInLimit;
    //输出带宽控制 enable(1), disable(2)
    private Boolean ethifOutLimitCtrl;
    //输出带宽控制粒度
    private Integer ethifOutLimit;
}
