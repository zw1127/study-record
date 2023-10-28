package cn.javastudy.springboot.simulator.netconf.service;

import cn.javastudy.springboot.simulator.netconf.domain.NotificationJob;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.PeriodTaskHandler;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import org.w3c.dom.Document;

public interface SimulateNotificationService {

    ListenableFuture<Boolean> sendNotification(Document notificationContent,
                                               String deviceId,
                                               String targetIp,
                                               Integer targetPort);

    String addJob(NotificationJob job);

    boolean deleteJob(String jobKey);

    Map<String, PeriodTaskHandler> allJobs();

}
