package cn.javastudy.springboot.simulator.netconf.device;

import static cn.javastudy.springboot.simulator.netconf.utils.Utils.resolveHeader;

import cn.javastudy.springboot.simulator.netconf.domain.DeviceUniqueInfo;
import cn.javastudy.springboot.simulator.netconf.utils.Utils;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.opendaylight.netconf.api.messages.NetconfHelloMessageAdditionalHeader;
import org.opendaylight.netconf.api.monitoring.NetconfManagementSession;
import org.opendaylight.netconf.api.monitoring.SessionEvent;
import org.opendaylight.netconf.api.monitoring.SessionListener;
import org.opendaylight.netconf.impl.NetconfServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceSessionManager implements SessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceSessionManager.class);

    private final Map<InetSocketAddress, NetconfServerSession> sessionMap = new ConcurrentHashMap<>();

    private final String deviceId;

    public DeviceSessionManager(DeviceUniqueInfo uniqueInfo) {
        this.deviceId = uniqueInfo.getUniqueKey();
    }

    @Override
    public void onSessionUp(NetconfManagementSession session) {
        NetconfHelloMessageAdditionalHeader header = resolveHeader(session);
        LOG.info("device:{} session up, remote info:{}", deviceId, header);
        if (header == null) {
            LOG.warn("resolve session:{} header is null.", session);
            return;
        }

        InetSocketAddress inetAddress = Utils.getInetAddress(header.getAddress(), header.getPort());
        if (session instanceof NetconfServerSession) {
            sessionMap.put(inetAddress, (NetconfServerSession) session);
            return;
        }

        LOG.warn("onSessionUp session:{} is not NetconfServerSession", session);
    }

    @Override
    public void onSessionDown(NetconfManagementSession session) {
        NetconfHelloMessageAdditionalHeader header = resolveHeader(session);
        LOG.info("device:{} session down, remote info:{}", deviceId, header);
        if (header == null) {
            LOG.warn("device:{} session down, resolve session:{} header is null.", deviceId, session);
            return;
        }

        InetSocketAddress inetAddress = Utils.getInetAddress(header.getAddress(), header.getPort());
        if (session instanceof NetconfServerSession) {
            sessionMap.remove(inetAddress, (NetconfServerSession) session);
            return;
        }


        LOG.warn("device:{} session down session:{} is not NetconfServerSession", deviceId, session);
    }

    @Override
    public void onSessionEvent(SessionEvent event) {
        LOG.debug("device:{} on SessionEvent event type: {}, session:{}",
            deviceId, event.getType(), event.getSession().toManagementSession());
    }

    public Map<InetSocketAddress, NetconfServerSession> getSessions() {
        return Map.copyOf(sessionMap);
    }
}
