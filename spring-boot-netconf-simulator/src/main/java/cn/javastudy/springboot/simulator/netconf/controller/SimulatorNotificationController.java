package cn.javastudy.springboot.simulator.netconf.controller;

import cn.javastudy.springboot.simulator.netconf.domain.JobInfo;
import cn.javastudy.springboot.simulator.netconf.domain.NotificationJob;
import cn.javastudy.springboot.simulator.netconf.service.SimulateNotificationService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.util.concurrent.ListenableFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/simulator/notification")
@Tag(name = "模拟器Notification管理")
public class SimulatorNotificationController {

    @Resource
    private SimulateNotificationService simulateNotificationService;

    @ApiOperationSupport(order = 1)
    @PostMapping(value = "/send-notification/{uniqueKey}/{targetIp}/{targetPort}",
        consumes = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "发送Notification")
    public String sendNotification(@PathVariable("uniqueKey") String uniqueKey,
                                   @PathVariable("targetIp") String targetIp,
                                   @PathVariable("targetPort") Integer targetPort,
                                   @RequestBody String notificationXml) {
        try {
            Document document = XmlUtil.readXmlToDocument(notificationXml);
            ListenableFuture<Boolean> future =
                simulateNotificationService.sendNotification(document, uniqueKey, targetIp, targetPort);
            return future.get() ? "successful" : "failed";
        } catch (SAXException | IOException | InterruptedException | ExecutionException e) {
            return "failed:" + e.getMessage();
        }
    }

    @SuppressWarnings("IllegalCatch")
    @ApiOperationSupport(order = 2)
    @PostMapping(value = "/add-send-notification-job/{uniqueKey}/{targetIp}/{targetPort}/{period}",
        consumes = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "添加发送Notification任务")
    public String addNotificationJob(@PathVariable("uniqueKey") String uniqueKey,
                                     @PathVariable("targetIp") String targetIp,
                                     @PathVariable("targetPort") Integer targetPort,
                                     @PathVariable("period") Long period,
                                     @RequestBody String notificationXml) {
        try {
            NotificationJob job = new NotificationJob();

            job.setDeviceId(uniqueKey);
            job.setTargetIp(targetIp);
            job.setTargetPort(targetPort);
            job.setPeriod(period);
            job.setNotificationXml(notificationXml);

            String jobKey = simulateNotificationService.addJob(job);
            return jobKey + " add successful";
        } catch (Throwable e) {
            return "failed:" + e.getMessage();
        }
    }

    @ApiOperationSupport(order = 3)
    @GetMapping("/notification-job-list/")
    @Operation(summary = "查询所的发送Notification任务列表")
    public List<JobInfo> listNotificationJobs() {
        return Optional.ofNullable(simulateNotificationService.allJobs())
            .orElse(Collections.emptyMap())
            .entrySet()
            .stream()
            .map(entry -> new JobInfo(entry.getKey(), entry.getValue().state()))
            .collect(Collectors.toList());
    }

    @SuppressWarnings("IllegalCatch")
    @ApiOperationSupport(order = 4)
    @DeleteMapping(value = "/delete-notification-job/{jobKey}")
    @Operation(summary = "删除发送Notification任务")
    public String deleteNotificationJob(@PathVariable("jobKey") String jobKey) {
        try {
            boolean deleted = simulateNotificationService.deleteJob(jobKey);
            String result = deleted ? "successful" : "failed";
            return "delete job:" + jobKey + ", result: " + result;
        } catch (Throwable e) {
            return "failed:" + e.getMessage();
        }
    }

}
