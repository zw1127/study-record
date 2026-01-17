package cn.javastudy.springboot.netconf.client.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class NetconfDeviceInfo {

    private final String deviceId;
    private final String username;
    private final String password;
    private final String address;
    private final Integer port;

    private final Long connectionTimeoutMillis;
    private final Long maxConnectionAttempts;
    private final Integer betweenAttemptsTimeoutMillis;
    private final BigDecimal sleepFactor;

    @JsonCreator
    public NetconfDeviceInfo(@JsonProperty("username") String deviceId,
                             @JsonProperty("username") String username,
                             @JsonProperty("password") String password,
                             @JsonProperty("address") String address,
                             @JsonProperty("port") Integer port) {
        this(deviceId, username, password, address, port, null, null, null, null);
    }

    public NetconfDeviceInfo(String deviceId,
                             String username,
                             String password,
                             String address,
                             Integer port,
                             Long connectionTimeoutMillis,
                             Long maxConnectionAttempts,
                             Integer betweenAttemptsTimeoutMillis,
                             BigDecimal sleepFactor) {
        this.deviceId = deviceId;
        this.username = username;
        this.password = password;
        this.address = address;
        this.port = port;
        this.connectionTimeoutMillis = connectionTimeoutMillis;
        this.maxConnectionAttempts = maxConnectionAttempts;
        this.betweenAttemptsTimeoutMillis = betweenAttemptsTimeoutMillis;
        this.sleepFactor = sleepFactor;
    }

}
