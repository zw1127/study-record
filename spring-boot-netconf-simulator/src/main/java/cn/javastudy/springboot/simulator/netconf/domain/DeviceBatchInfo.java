package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceBatchInfo {

    @JsonProperty("device-id-prefix")
    private String deviceIdPrefix;

    @JsonProperty("device-id-start")
    private Integer deviceIdStart;

    @JsonProperty("port-start")
    private Integer portStart;

    @JsonProperty("batch-size")
    private Integer batchSize;

}
