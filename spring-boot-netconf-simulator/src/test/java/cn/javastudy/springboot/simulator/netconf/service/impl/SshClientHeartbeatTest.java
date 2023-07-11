package cn.javastudy.springboot.simulator.netconf.service.impl;

import static com.google.common.base.Verify.verify;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.opendaylight.netconf.nettyutil.handler.ssh.authentication.LoginPasswordHandler;
import org.opendaylight.netconf.nettyutil.handler.ssh.client.NetconfClientBuilder;
import org.opendaylight.netconf.nettyutil.handler.ssh.client.NetconfSshClient;
import org.opendaylight.netconf.nettyutil.handler.ssh.client.NettyAwareClientSession;
import org.opendaylight.netconf.shaded.sshd.client.future.AuthFuture;
import org.opendaylight.netconf.shaded.sshd.core.CoreModuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshClientHeartbeatTest {

    private static final Logger LOG = LoggerFactory.getLogger(SshClientHeartbeatTest.class);

    public static final int SSH_DEFAULT_NIO_WORKERS = 8;

    @Test
    public void testHeartbeat() throws Exception {
        NetconfSshClient sshClient = new NetconfClientBuilder().build();

        // Disable default timeouts from mina sshd
        sshClient.getProperties().put(CoreModuleProperties.AUTH_TIMEOUT.getName(), "0");
        sshClient.getProperties().put(CoreModuleProperties.IDLE_TIMEOUT.getName(), "10000");
        sshClient.getProperties().put(CoreModuleProperties.NIO2_READ_TIMEOUT.getName(), "0");
        sshClient.getProperties().put(CoreModuleProperties.TCP_NODELAY.getName(), true);
        sshClient.getProperties().put(CoreModuleProperties.HEARTBEAT_INTERVAL.getName(), "1000");

        sshClient.setNioWorkers(SSH_DEFAULT_NIO_WORKERS);
        sshClient.start();

        String username = "hp";
        LoginPasswordHandler authenticationHandler = new LoginPasswordHandler(username, "Fiberhome.2022");

        sshClient.connect(authenticationHandler.getUsername(), "10.190.49.224", 22)
            .verify(5, TimeUnit.SECONDS)
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
            }).wait();
    }
}
