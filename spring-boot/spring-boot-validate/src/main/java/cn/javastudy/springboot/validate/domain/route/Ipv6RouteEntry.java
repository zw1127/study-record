package cn.javastudy.springboot.validate.domain.route;

import lombok.Data;

@Data
public class Ipv6RouteEntry {

    //设备id(key)
    private String nodeId;
    //目的ip
    private String ipRouteDest;
    //掩码
    private String ipRouteMaskLength;
    //路由下一条ip
    private String ipRouteNextHop;
    //端口索引
    private String ipRouteIfIndex;
    //端口名称
    private String ifIndexName;
    //路由学习机制 other(1), invalid(2),direct(3), indirect(4)
    private String ipRouteProto;
    //cost
    private String ipRouteCost;
    //vpn
    private Boolean ipRouteVpnEnable;
    //vpn name
    private String ipRouteVpnName;
    //优先级，展示用，配置对比应该不需要？
    private Integer preference;

}
