package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceBatchBaseInfo {

    @JsonProperty("device-id-prefix")
    private String deviceIdPrefix;

    @JsonProperty("device-id-start")
    private Integer deviceIdStart;

    @JsonProperty("batch-size")
    private Integer batchSize;

    // deviceId长度，为0表示不是定长的
    @JsonProperty("device-id-length")
    private Integer deviceIdLength = 0;

}
