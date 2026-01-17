package cn.javastudy.springboot.netconf.client.service;

import cn.javastudy.springboot.netconf.client.entity.NetconfDeviceInfo;
import cn.javastudy.springboot.netconf.client.entity.RemoteDeviceId;
import com.google.common.util.concurrent.ListenableFuture;

public interface NetconfDeviceService {

    ListenableFuture<RemoteDeviceId> connectDevice(NetconfDeviceInfo deviceInfo);

    ListenableFuture<Void> disconnectDevice(String deviceId);
}
