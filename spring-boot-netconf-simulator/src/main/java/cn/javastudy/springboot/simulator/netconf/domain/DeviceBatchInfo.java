package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceBatchInfo extends DeviceBatchBaseInfo {

    @JsonProperty("port-start")
    private Integer portStart;

}
