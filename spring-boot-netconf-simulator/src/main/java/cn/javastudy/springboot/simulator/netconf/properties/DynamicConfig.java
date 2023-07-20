package cn.javastudy.springboot.simulator.netconf.properties;

import lombok.Data;

@Data
public class DynamicConfig {

    // xpath 路径
    private String path;

    // 小数位数，默认为0，表示整数
    private Integer scale = 0;

    // 开始数值大小，用String表示
    private String start;

    // 结束数值大小，用String表示
    private String end;

}
