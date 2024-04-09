package cn.javastudy.springboot.validate.domain.route;

import lombok.Data;

@Data
public class Ipv4RouteEntry {

    //设备id(key)
    private String nodeId;
    //目的ip
    private String ipRouteDest;
    //掩码
    private String ipRouteMask;
    //端口索引
    private String ipRouteIfIndex;
    //端口名称
    private String ifIndexName;
    //路由下一条ip
    private String ipRouteNextHop;
    //路由学习机制 other(1), invalid(2),direct(3), indirect(4)
    private String ipRouteProto;
    //路由Metric
    private Integer ipRouteMetric;

}
