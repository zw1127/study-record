package cn.javastudy.springboot.snmp.service;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ObjectUtils.requireNonEmpty;

import cn.javastudy.springboot.snmp.data.AuthenticationProtocol;
import cn.javastudy.springboot.snmp.data.PrivacyProtocol;
import cn.javastudy.springboot.snmp.data.SnmpProfileConfiguration;
import cn.javastudy.springboot.snmp.data.SnmpProtocolVersion;
import cn.javastudy.springboot.snmp.data.SnmpTransportConfiguration;
import java.util.Optional;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.AbstractTarget;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.springframework.stereotype.Service;

@Service
public class SnmpAuthService {

    @Resource
    private SnmpTransportService snmpTransportService;

    public Target<UdpAddress> setUpSnmpTarget(SnmpProfileConfiguration profileConfig,
                                              SnmpTransportConfiguration transportConfig) {
        AbstractTarget<UdpAddress> target;

        UdpAddress address =
            Optional.ofNullable(GenericAddress.parse("udp:" + transportConfig.getHost() + "/" + transportConfig.getPort()))
                .map(UdpAddress.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Address of the SNMP device is invalid"));

        SnmpProtocolVersion protocolVersion = transportConfig.getProtocolVersion();
        switch (protocolVersion) {
            case V1:
                CommunityTarget<UdpAddress> communityTargetV1 = new CommunityTarget<>();

                communityTargetV1.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv1);
                communityTargetV1.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
                communityTargetV1.setCommunity(new OctetString(transportConfig.getCommunity()));

                target = communityTargetV1;
                break;
            case V2C:
                CommunityTarget<UdpAddress> communityTargetV2 = new CommunityTarget<>();

                communityTargetV2.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv2c);
                communityTargetV2.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
                communityTargetV2.setCommunity(new OctetString(transportConfig.getCommunity()));

                target = communityTargetV2;
                break;
            case V3:
                OctetString username = new OctetString(transportConfig.getUsername());
                OctetString securityName = new OctetString(transportConfig.getSecurityName());

                OctetString engineId;
                if (StringUtils.isNotEmpty(transportConfig.getEngineId())) {
                    engineId = new OctetString(transportConfig.getEngineId());
                } else {
                    byte[] targetEngineID = snmpTransportService.getSnmp().discoverAuthoritativeEngineID(address, 1000);
                    engineId = new OctetString(targetEngineID);
                }

                UsmUser usmUser = buildUsmUser(transportConfig);
                USM usm = new USM();
                if (usm.hasUser(engineId, securityName)) {
                    usm.removeAllUsers(username, engineId);
                }
                usm.addUser(usmUser);

                UserTarget<UdpAddress> userTarget = new UserTarget<>();

                userTarget.setAuthoritativeEngineID(engineId.getValue());
                userTarget.setSecurityName(securityName);
                userTarget.setSecurityModel(SecurityModel.SECURITY_MODEL_USM);
                userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
                target = userTarget;
                break;
            default:
                throw new UnsupportedOperationException("SNMP protocol version " + protocolVersion + " is not supported");
        }

        target.setAddress(address);
        target.setTimeout(profileConfig.getTimeoutMs());
        target.setRetries(profileConfig.getRetries());
        target.setVersion(protocolVersion.getCode());

        return target;
    }

    private static UsmUser buildUsmUser(SnmpTransportConfiguration transportConfig) {
        String securityUser = transportConfig.getSecurityName();
        requireNonEmpty(securityUser, "SecurityUser is null or empty");
        OctetString securityName = new OctetString(securityUser);

        AuthenticationProtocol authenticationProtocol = transportConfig.getAuthenticationProtocol();
        requireNonNull(authenticationProtocol, "AuthenticationProtocol is null");

        PrivacyProtocol privacyProtocol = transportConfig.getPrivacyProtocol();
        requireNonNull(privacyProtocol, "PrivacyProtocol is null");

        return new UsmUser(securityName,
            authenticationProtocol.getOid(),
            OctetString.fromString(transportConfig.getAuthenticationPassphrase()),
            privacyProtocol.getOid(),
            OctetString.fromString(transportConfig.getPrivacyPassphrase()));
    }

//    public void cleanUpSnmpAuthInfo(DeviceSessionContext sessionContext) {
//        SnmpDeviceTransportConfiguration deviceTransportConfiguration = sessionContext.getDeviceTransportConfiguration();
//        if (deviceTransportConfiguration.getProtocolVersion() == SnmpProtocolVersion.V3) {
//            OctetString username = new OctetString(deviceTransportConfiguration.getUsername());
//            OctetString engineId = new OctetString(deviceTransportConfiguration.getEngineId());
//            snmpTransportService.getSnmp().getUSM().removeAllUsers(username, engineId);
//        }
//    }
}
