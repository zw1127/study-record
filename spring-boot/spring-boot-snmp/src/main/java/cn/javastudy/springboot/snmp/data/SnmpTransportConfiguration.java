package cn.javastudy.springboot.snmp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString(of = {"host", "port", "protocolVersion"})
public class SnmpTransportConfiguration {

    private String host;
    private Integer port;
    private SnmpProtocolVersion protocolVersion;

    /*
     * For SNMP v1 and v2c
     * */
    private String community;

    /*
     * For SNMP v3
     * */
    private String username;
    private String securityName;
    private String contextName;
    private AuthenticationProtocol authenticationProtocol;
    private String authenticationPassphrase;
    private PrivacyProtocol privacyProtocol;
    private String privacyPassphrase;
    private String engineId;

    public SnmpTransportConfiguration() {
        this.host = "localhost";
        this.port = 161;
        this.protocolVersion = SnmpProtocolVersion.V2C;
        this.community = "public";
    }

    public void validate() {
        if (!isValid()) {
            throw new IllegalArgumentException("Snmp Transport configuration is not valid");
        }
    }

    @JsonIgnore
    private boolean isValid() {
        boolean isValid = StringUtils.isNotBlank(host) && port != null && protocolVersion != null;
        if (isValid) {
            switch (protocolVersion) {
                case V1:
                case V2C:
                    isValid = StringUtils.isNotEmpty(community);
                    break;
                case V3:
                    isValid = StringUtils.isNotBlank(username) && StringUtils.isNotBlank(securityName)
                        && contextName != null && authenticationProtocol != null
                        && StringUtils.isNotBlank(authenticationPassphrase)
                        && privacyProtocol != null && StringUtils.isNotBlank(privacyPassphrase) && engineId != null;
                    break;
            }
        }
        return isValid;
    }
}
