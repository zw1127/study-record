package cn.javastudy.springboot.snmp.service;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ObjectUtils.requireNonEmpty;

import cn.javastudy.springboot.snmp.data.AuthenticationProtocol;
import cn.javastudy.springboot.snmp.data.PrivacyProtocol;
import cn.javastudy.springboot.snmp.data.SnmpProfileConfiguration;
import cn.javastudy.springboot.snmp.data.SnmpProtocolVersion;
import cn.javastudy.springboot.snmp.data.SnmpTransportConfiguration;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.AbstractTarget;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.springframework.stereotype.Service;

@Service
public class SnmpAuthService {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpAuthService.class);

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
                OctetString securityName = new OctetString(transportConfig.getSecurityName());

                UserTarget<UdpAddress> userTarget = new UserTarget<>();

                userTarget.setSecurityName(securityName);
                userTarget.setSecurityModel(SecurityModel.SECURITY_MODEL_USM);
                userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);

                String engineId = transportConfig.getEngineId();
                if (StringUtils.isEmpty(engineId)) {
                    byte[] authoritativeEngineID =
                        snmpTransportService.getSnmp().discoverAuthoritativeEngineID(address, 1000);
                    requireNonNull(authoritativeEngineID, address + " discoverAuthoritativeEngineID is null");
                    userTarget.setAuthoritativeEngineID(authoritativeEngineID);
                } else {
                    OctetString authoritativeEngineID = resolveEngineId(engineId);
                    userTarget.setAuthoritativeEngineID(authoritativeEngineID.getValue());
                }
                byte[] authoritativeEngineID = userTarget.getAuthoritativeEngineID();

                AuthenticationProtocol authenticationProtocol = transportConfig.getAuthenticationProtocol();
                requireNonNull(authenticationProtocol, "AuthenticationProtocol is null");

                PrivacyProtocol privacyProtocol = transportConfig.getPrivacyProtocol();
                requireNonNull(privacyProtocol, "PrivacyProtocol is null");

                int securityLevel = resolveSecurityLevel(authenticationProtocol, privacyProtocol);
                userTarget.setSecurityLevel(securityLevel);

                USM usm = snmpTransportService.getSnmp().getUSM();
                addUsmUser(usm, transportConfig, authoritativeEngineID);

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

    private static int resolveSecurityLevel(AuthenticationProtocol authProtocol, PrivacyProtocol privProtocol) {
        if (PrivacyProtocol.NONE.equals(privProtocol)) {
            if (AuthenticationProtocol.NONE.equals(authProtocol)) {
                return SecurityLevel.noAuthNoPriv.getSnmpValue();
            } else {
                return SecurityLevel.authNoPriv.getSnmpValue();
            }
        } else {
            if (!AuthenticationProtocol.NONE.equals(authProtocol)) {
                return SecurityLevel.authPriv.getSnmpValue();
            }
        }

        LOG.warn("AuthenticationProtocol is:{}, PrivacyProtocol is:{}", authProtocol, privProtocol);
        return SecurityLevel.undefined.getSnmpValue();
    }

    public static OctetString resolveEngineId(String engineId) {
        char delimiter = ':';
        if (StringUtils.contains(engineId, delimiter)) {
            return OctetString.fromHexString(engineId, delimiter);
        }

        return OctetString.fromHexStringPairs(engineId);
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

    public static void addUsmUser(USM usm, SnmpTransportConfiguration param, byte[] authoritativeEngineID) {
        String securityUser = requireNonNull(param.getSecurityName(), "SNMP v3 SecurityUser is null");
        OctetString securityName = new OctetString(securityUser);

        AuthenticationProtocol authenticationProtocol = param.getAuthenticationProtocol();
        requireNonNull(authenticationProtocol, "AuthenticationProtocol is null");

        PrivacyProtocol privacyProtocol = param.getPrivacyProtocol();
        requireNonNull(privacyProtocol, "PrivacyProtocol is null");

        OID authProtocolOid = authenticationProtocol.getOid();
        OID privProtocolOid = privacyProtocol.getOid();

        OctetString authPassphrase = OctetString.fromString(param.getAuthenticationPassphrase());
        authPassphrase = passwordToKey(authProtocolOid, authPassphrase, authoritativeEngineID);

        OctetString privPassphrase = OctetString.fromString(param.getPrivacyPassphrase());
        privPassphrase = passwordToKey(privProtocolOid, authProtocolOid, privPassphrase, authoritativeEngineID);

        OctetString engineId = OctetString.fromByteArray(authoritativeEngineID);
        if (usm.hasUser(engineId, securityName)) {
            usm.removeAllUsers(securityName, engineId);
        }

        usm.addLocalizedUser(
            authoritativeEngineID, securityName,
            authProtocolOid, authPassphrase.getValue(),
            privProtocolOid, privPassphrase.getValue()
        );
    }

    private static OctetString passwordToKey(OID authProtocolID, OctetString passwordString, byte[] engineID) {
        if (authProtocolID == null || passwordString == null || passwordString.length() == 0) {
            return new OctetString();
        }

        if (ArrayUtils.isEmpty(engineID)) {
            return passwordString;
        }

        return OctetString.fromByteArray(SecurityProtocols.getInstance().passwordToKey(
            authProtocolID, passwordString, engineID));
    }

    private static OctetString passwordToKey(OID privProtocolID,
                                             OID authProtocolID,
                                             OctetString passwordString,
                                             byte[] engineID) {
        if (privProtocolID == null
            || authProtocolID == null
            || passwordString == null
            || passwordString.length() == 0) {
            return new OctetString();
        }

        if (ArrayUtils.isEmpty(engineID)) {
            return passwordString;
        }

        return OctetString.fromByteArray(SecurityProtocols.getInstance().passwordToKey(
            privProtocolID, authProtocolID, passwordString, engineID));
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
