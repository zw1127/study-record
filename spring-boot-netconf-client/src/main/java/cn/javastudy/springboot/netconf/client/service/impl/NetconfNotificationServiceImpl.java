package cn.javastudy.springboot.netconf.client.service.impl;

import cn.javastudy.springboot.netconf.client.entity.RemoteDeviceId;
import cn.javastudy.springboot.netconf.client.service.NetconfNotificationService;
import org.opendaylight.netconf.api.NetconfMessage;

public class NetconfNotificationServiceImpl implements NetconfNotificationService {

    @Override
    public void dispatchNotification(RemoteDeviceId id, NetconfMessage notification) {

    }
}
