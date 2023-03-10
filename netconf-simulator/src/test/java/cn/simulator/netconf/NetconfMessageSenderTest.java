package cn.simulator.netconf;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.netconf.api.NetconfMessage;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.opendaylight.netconf.client.NetconfClientDispatcherImpl;
import org.opendaylight.netconf.client.NetconfClientSession;
import org.opendaylight.netconf.client.SimpleNetconfClientSessionListener;
import org.opendaylight.netconf.client.conf.NetconfClientConfiguration;
import org.opendaylight.netconf.client.conf.NetconfClientConfigurationBuilder;
import org.opendaylight.netconf.nettyutil.NeverReconnectStrategy;
import org.opendaylight.netconf.nettyutil.handler.ssh.authentication.AuthenticationHandler;
import org.opendaylight.netconf.nettyutil.handler.ssh.authentication.LoginPasswordHandler;
import org.w3c.dom.Document;

public class NetconfMessageSenderTest {

    private static final long CONNECT_TIMEOUT = 15;

    private final SimpleNetconfClientSessionListener sessionListener = new SimpleNetconfClientSessionListener();

    @Before
    public void setUp() throws Exception {
        ThreadFactory timerFactory = new ThreadFactoryBuilder()
            .setNameFormat("netconf-message-sender-hashedwheeltimer-%d")
            .build();
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(timerFactory);

        int corePoolSize = 20;
        int maximumPoolSize = 20;
        long keepAliveTime = 60;
        TimeUnit unit = TimeUnit.SECONDS;
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("netconf-connection-check-pool-%d")
            .build();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(2000);
        Executor executor = new ThreadPoolExecutor(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);

        NioEventLoopGroup nettyGroup = new NioEventLoopGroup(0, executor);

        NetconfClientDispatcherImpl netconfClientDispatcher =
            new NetconfClientDispatcherImpl(nettyGroup, nettyGroup, hashedWheelTimer);

        String host = "10.190.23.232";
        int port = 851;
//        String userName = "fiberhome";
//        String pw = "fiberhome";
        String userName = "BaiduOLS";
        String pw = "Baidu_ott@123";
        AuthenticationHandler authHandler = new LoginPasswordHandler(userName, pw);
        NetconfClientConfiguration config = getClientConfig(host, port, authHandler);

        Future<NetconfClientSession> client = netconfClientDispatcher.createClient(config);
        client.get(CONNECT_TIMEOUT, TimeUnit.SECONDS);

    }

    @Test
    public void testSendMessage() throws Exception {
        Document document = XmlUtil.readXmlToDocument(
            NetconfMessageSenderTest.class.getResourceAsStream("/message/get-components.xml"));
        NetconfMessage netconfMessage = new NetconfMessage(document);

        Future<NetconfMessage> channelFuture = sessionListener.sendRequest(netconfMessage);

        NetconfMessage response = channelFuture.get();
        Assert.assertNotNull(response);
    }

    private NetconfClientConfiguration getClientConfig(String host, int port,
                                                       AuthenticationHandler authHandler) throws UnknownHostException {
        InetSocketAddress netconfAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        final NetconfClientConfigurationBuilder b = NetconfClientConfigurationBuilder.create();
        b.withAddress(netconfAddress);

        b.withSessionListener(sessionListener);
        b.withReconnectStrategy(new NeverReconnectStrategy(GlobalEventExecutor.INSTANCE,
            NetconfClientConfigurationBuilder.DEFAULT_CONNECTION_TIMEOUT_MILLIS));
        b.withProtocol(NetconfClientConfiguration.NetconfClientProtocol.SSH);
        b.withAuthHandler(authHandler);
        return b.build();
    }


}
