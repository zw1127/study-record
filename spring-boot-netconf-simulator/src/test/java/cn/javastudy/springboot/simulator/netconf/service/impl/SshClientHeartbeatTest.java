package cn.javastudy.springboot.simulator.netconf.service.impl;

import static com.google.common.base.Verify.verify;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.opendaylight.netconf.nettyutil.handler.ssh.authentication.LoginPasswordHandler;
import org.opendaylight.netconf.nettyutil.handler.ssh.client.NetconfClientBuilder;
import org.opendaylight.netconf.nettyutil.handler.ssh.client.NetconfSshClient;
import org.opendaylight.netconf.nettyutil.handler.ssh.client.NettyAwareClientSession;
import org.opendaylight.netconf.shaded.sshd.client.future.AuthFuture;
import org.opendaylight.netconf.shaded.sshd.common.PropertyResolverUtils;
import org.opendaylight.netconf.shaded.sshd.common.io.BuiltinIoServiceFactoryFactories;
import org.opendaylight.netconf.shaded.sshd.core.CoreModuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SshClientHeartbeatTest {

    private static final Logger LOG = LoggerFactory.getLogger(SshClientHeartbeatTest.class);

    public static final int SSH_DEFAULT_NIO_WORKERS = 8;

    @Test
    public void testHeartbeat() throws Exception {
        NetconfSshClient sshClient = new NetconfClientBuilder().build();

        sshClient.setIoServiceFactoryFactory(BuiltinIoServiceFactoryFactories.NETTY.create());

        // Disable default timeouts from mina sshd
        sshClient.getProperties().put(CoreModuleProperties.AUTH_TIMEOUT.getName(), "0");
        sshClient.getProperties().put(CoreModuleProperties.IDLE_TIMEOUT.getName(), "0");
        sshClient.getProperties().put(CoreModuleProperties.NIO2_READ_TIMEOUT.getName(), "30000");
        sshClient.getProperties().put(CoreModuleProperties.TCP_NODELAY.getName(), true);
//        sshClient.getProperties().put(CoreModuleProperties.HEARTBEAT_INTERVAL.getName(), "5000");

        PropertyResolverUtils.updateProperty(sshClient, "heartbeat-interval", 5000);
        PropertyResolverUtils.updateProperty(sshClient, "heartbeat-reply-wait", 50000);
        PropertyResolverUtils.updateProperty(sshClient, "heartbeat-request", "keepalive@openssh.com");

        sshClient.setNioWorkers(SSH_DEFAULT_NIO_WORKERS);
        sshClient.start();

        String username = "admin";
        LoginPasswordHandler authenticationHandler = new LoginPasswordHandler(username, "Admin_123");

        sshClient.connect(authenticationHandler.getUsername(), "10.190.23.232", 848)
            .verify(15, TimeUnit.SECONDS)
            .addListener(connectFuture -> {
                final var clientSession = connectFuture.getSession();
                verify(clientSession instanceof NettyAwareClientSession, "Unexpected session %s", clientSession);

                final var localSession = (NettyAwareClientSession) clientSession;

                final AuthFuture authFuture;
                try {
                    authFuture = authenticationHandler.authenticate(localSession);
                } catch (final IOException e) {
                    LOG.warn("authenticate failed.", e);
                    return;
                }

                authFuture.addListener(future -> {
                    final var cause = future.getException();
                    if (cause != null) {
                        LOG.warn("authenticate failed.", cause);
                        return;
                    }

                    LOG.info("open ssh successful.");
                });
            });
        TimeUnit.DAYS.sleep(1);
    }
}
