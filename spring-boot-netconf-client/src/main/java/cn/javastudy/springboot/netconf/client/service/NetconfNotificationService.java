package cn.javastudy.springboot.netconf.client.service;

import cn.javastudy.springboot.netconf.client.entity.RemoteDeviceId;
import org.opendaylight.netconf.api.NetconfMessage;

public interface NetconfNotificationService {

    void dispatchNotification(final RemoteDeviceId id, final NetconfMessage notification);
}
