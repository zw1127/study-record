package cn.javastudy.springboot.simulator.netconf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJob {

    @JsonProperty("job-id")
    private String jobId;

    @JsonProperty("device-id")
    private String deviceId;

    @JsonProperty("target-ip")
    private String targetIp;

    @JsonProperty("target-port")
    private Integer targetPort;

    @JsonProperty("period")
    private Long period;

    @JsonProperty("notification-xml")
    private String notificationXml;

}
