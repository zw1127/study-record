package cn.javastudy.springboot.snmp.service;

import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
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
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

@Ignore
public class SnmpTrapReceiverTest {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpTrapReceiverTest.class);

    private static final String RECEIVER_THREAD_POOL_NAME = "fh-snmp-receiver";

    @Before
    public void setUp() {
        SNMP4JSettings.setCheckUsmUserPassphraseLength(false);
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
    }

    @Test
    public void snmpV3TrapReceiverTest() throws Exception {
        USM receviveUsm = initSnmpV3Usm("trap-receiver");

        String ipAddress = "0.0.0.0";
        int port = 10162;
        UdpAddress listenAddress = (UdpAddress) GenericAddress.parse("udp:" + ipAddress + "/" + port);

        int receiverPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPool receiverThreadPool = ThreadPool.create(RECEIVER_THREAD_POOL_NAME, receiverPoolSize);
        MessageDispatcher receiverDispatcher =
            new MultiThreadedMessageDispatcher(receiverThreadPool, new MessageDispatcherImpl());

        receiverDispatcher.addMessageProcessingModel(new MPv1());
        receiverDispatcher.addMessageProcessingModel(new MPv2c());
        receiverDispatcher.addMessageProcessingModel(new MPv3(receviveUsm));

        TransportMapping<UdpAddress> receiverTransport = new DefaultUdpTransportMapping(listenAddress);
        Snmp snmpReceiver = new Snmp(receiverDispatcher, receiverTransport);
        snmpReceiver.listen();

        String securityUser = "123";
        String authPassphrase = "1234567";
        String privPassphrase = "1234567";
        String authoritativeEngineID = "80000EDF03803AF4AF785A";

        OctetString engineId = OctetString.fromHexStringPairs(authoritativeEngineID);

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

        if (receviveUsm.hasUser(engineId, securityName)) {
            receviveUsm.removeAllUsers(securityName, engineId);
        }
        receviveUsm.addLocalizedUser(engineId.getValue(), usmUser.getSecurityName(),
            usmUser.getAuthenticationProtocol(), usmUser.getAuthenticationPassphrase().getValue(),
            usmUser.getPrivacyProtocol(), usmUser.getPrivacyPassphrase().getValue());

        snmpReceiver.addCommandResponder(new CommandResponder() {
            @Override
            public <A extends Address> void processPdu(CommandResponderEvent<A> event) {
                LOG.info("receive trap:{}", event);
            }
        });
        TimeUnit.DAYS.sleep(1);
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


    private USM initSnmpV3Usm(String module) {
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID(OctetString.fromString(module)));
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);

        LOG.info("init snmp v3 usm successful.");
        return usm;
    }
}
