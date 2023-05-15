package cn.javastudy.springboot.snmp.service;

import static cn.javastudy.springboot.snmp.utils.SnmpConstans.DEFAULT_LISTENER_PORT;
import static cn.javastudy.springboot.snmp.utils.SnmpConstans.DEFAULT_POOL_SIZE;

import cn.javastudy.springboot.snmp.configuration.SnmpProperties;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.stereotype.Service;

@Service
public class SnmpTransportService implements CommandResponder {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpTransportService.class);
    private static final String THREAD_POOL_NAME = "study-snmp";
    private static final String DEFAULT_LISTENER_ADDRESS = "0.0.0.0";
    private Snmp snmp;

    @Resource
    private SnmpProperties properties;

    @PostConstruct
    public void init() {
        try {
            int poolSize = properties == null ? DEFAULT_POOL_SIZE : properties.getPoolSize();
            ThreadPool threadPool = ThreadPool.create(THREAD_POOL_NAME, poolSize);
            MessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

            UdpAddress listenAddress = resolveListenAddress();
            TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping(listenAddress);

            boolean checkUsmUserPassphraseLength = properties != null && properties.isCheckUsmUserPassphraseLength();
            SNMP4JSettings.setCheckUsmUserPassphraseLength(checkUsmUserPassphraseLength);
            snmp = new Snmp(dispatcher, transport);
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());

            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
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

            snmp.listen();
            snmp.addCommandResponder(this);
            LOG.info("init snmp agent successful.");
        } catch (IOException e) {
            LOG.error("init snmp agent error.", e);
        }
    }

    private UdpAddress resolveListenAddress() {
        String address = properties != null ? properties.getListenAddress() : DEFAULT_LISTENER_ADDRESS;
        String listenPort = properties != null ? properties.getListenPort() : DEFAULT_LISTENER_PORT;
        LOG.info("listen udp address:{}, port:{}", address, listenPort);

        return (UdpAddress) GenericAddress.parse("udp:" + address + "/" + listenPort);
    }

    @Override
    public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {

    }

    public Snmp getSnmp() {
        return snmp;
    }
}
