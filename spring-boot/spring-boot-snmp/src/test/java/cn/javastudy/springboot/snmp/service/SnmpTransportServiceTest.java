package cn.javastudy.springboot.snmp.service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthHMAC128SHA224;
import org.snmp4j.security.AuthHMAC192SHA256;
import org.snmp4j.security.AuthHMAC256SHA384;
import org.snmp4j.security.AuthHMAC384SHA512;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

@Ignore
public class SnmpTransportServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpTransportServiceTest.class);
    private static final String trapOid = ".1.3.6.1.2.1.1.6";

    @Test
    public void sendSnmpV3Trap() throws Exception {
        String ipAddress = "127.0.0.1";
        int port = 10162;
        UdpAddress targetAddress = (UdpAddress) GenericAddress.parse("udp:" + ipAddress + "/" + port);

        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);

        String authoritativeEngineID = "80000EDF03803AF4AF785A";

        OctetString engineId = OctetString.fromHexStringPairs(authoritativeEngineID);

        USM usm = initSnmpV3Usm(engineId);

        MPv3 mPv3 = (MPv3) snmp.getMessageDispatcher().getMessageProcessingModel(MPv3.ID);
        mPv3.setLocalEngineID(engineId.getValue());

        transport.listen();

        String securityUser = "123";
        String authPassphrase = "1234567";
        String privPassphrase = "1234567";

        OctetString securityName = new OctetString(securityUser);
        OID authenticationProtocol = AuthSHA.ID;
        OctetString authenticationPassphrase = new OctetString(authPassphrase);
        authenticationPassphrase = passwordToKey(authenticationProtocol, authenticationPassphrase, engineId.getValue());

        OID privacyProtocol = PrivAES128.ID;
        OctetString privacyPassphrase = new OctetString(privPassphrase);
        privacyPassphrase =
            passwordToKey(privacyProtocol, authenticationProtocol, privacyPassphrase, engineId.getValue());

        // SNMPv3 user settings
        UsmUser usmUser = new UsmUser(
            securityName,
            authenticationProtocol,
            authenticationPassphrase,
            privacyProtocol,
            privacyPassphrase
        );

        if (usm.hasUser(engineId, securityName)) {
            usm.removeAllUsers(securityName, engineId);
        }
        usm.addLocalizedUser(engineId.getValue(), usmUser.getSecurityName(),
            usmUser.getAuthenticationProtocol(), usmUser.getAuthenticationPassphrase().getValue(),
            usmUser.getPrivacyProtocol(), usmUser.getPrivacyPassphrase().getValue());

        // Set the USM to the SNMP object
        snmp.getUSM().addUser(usmUser.getSecurityName(), usmUser);

        // Create Target
        UserTarget<UdpAddress> target = new UserTarget<>();
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(securityName);
        target.setAuthoritativeEngineID(engineId.getValue());

        for (int i = 0; i < 100; i++) {
            ScopedPDU pdu = new ScopedPDU();

            pdu.setContextEngineID(engineId);
            pdu.setType(ScopedPDU.NOTIFICATION);
            TimeTicks sysUpTime = new TimeTicks(Instant.now().getEpochSecond());
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime, sysUpTime));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));

            // Send the PDU
            snmp.send(pdu, target);
            // Create PDU for V3
            snmp.addCommandResponder(new CommandResponder() {
                @Override
                public <A extends Address> void processPdu(CommandResponderEvent<A> event) {
                    LOG.info("send trap to: {} successful.", targetAddress);
                }
            });

            TimeUnit.SECONDS.sleep(10);
        }
    }

    private static OctetString passwordToKey(OID authProtocolID, OctetString passwordString, byte[] engineID) {
        if (authProtocolID == null || passwordString == null || passwordString.length() == 0) {
            return new OctetString();
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

        return OctetString.fromByteArray(SecurityProtocols.getInstance().passwordToKey(
            privProtocolID, authProtocolID, passwordString, engineID));
    }

    private USM initSnmpV3Usm(OctetString localEngineID) {
        SNMP4JSettings.setCheckUsmUserPassphraseLength(false);
//        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID(OctetString.fromString(module)));
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthSHA());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC128SHA224());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC192SHA256());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC256SHA384());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC384SHA512());

        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivDES());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES128());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES192());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256());

        LOG.info("init snmp v3 usm successful.");
        return usm;
    }


}
