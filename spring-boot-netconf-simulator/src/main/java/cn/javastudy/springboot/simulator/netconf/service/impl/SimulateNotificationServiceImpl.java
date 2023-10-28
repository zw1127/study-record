/*
 * Copyright (c) 2023 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.springboot.simulator.netconf.service.impl;

import cn.javastudy.springboot.simulator.netconf.domain.NotificationJob;
import cn.javastudy.springboot.simulator.netconf.scheduler.AsyncTask;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.JobScheduler;
import cn.javastudy.springboot.simulator.netconf.scheduler.api.PeriodTaskHandler;
import cn.javastudy.springboot.simulator.netconf.service.SimluateDeviceService;
import cn.javastudy.springboot.simulator.netconf.service.SimulateNotificationService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class SimulateNotificationServiceImpl implements SimulateNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(SimulateNotificationServiceImpl.class);

    private static final String SEPERATER = ":";

    private static final int FJP_MAX_CAP = 0x7fff;

    private static final Map<String, PeriodTaskHandler> JOB_MAP = new ConcurrentHashMap<>();

    @Resource
    private SimluateDeviceService simluateDeviceService;

    @Resource
    private JobScheduler jobScheduler;

    private ExecutorService executor;

    @PostConstruct
    public void init() {
        ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool -> {
            ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName(String.format("job-coordinator-executor-%s", worker.getPoolIndex()));
            return worker;
        };
        int cpus = Runtime.getRuntime().availableProcessors();
        executor = new ForkJoinPool(
            Math.max(Math.min(FJP_MAX_CAP, cpus - (cpus > 3 ? 2 : 1)), 1),
            factory,
            (th, ex) ->
                LOG.debug("thread {}({}) of job coordinator scheduler meet uncaught exception.",
                    th.getName(), th.getId(), ex),
            false);
    }

    @Override
    public ListenableFuture<Boolean> sendNotification(Document notificationContent,
                                                      String deviceId,
                                                      String targetIp,
                                                      Integer targetPort) {
        return simluateDeviceService.sendNotification(notificationContent, deviceId, targetIp, targetPort);
    }

    @Override
    public String addJob(NotificationJob job) {
        String jobKey = jobKey(job);
        job.setJobId(jobKey);

        PeriodTaskHandler oldHandler = JOB_MAP.get(jobKey);
        if (oldHandler != null) {
            LOG.warn("job:{} is exist, please stop first.", jobKey);
            return "job:" + jobKey + " exist";
        }

        PeriodTaskHandler periodTaskHandler = AsyncTask.fromCallable(() -> doSchedule(job))
            .intervalMillisecond(TimeUnit.SECONDS.toMillis(job.getPeriod()))
            .onSuccess(result -> LOG.trace("jobKey:{} execute success.", jobKey))
            .onError(ex -> LOG.info("jobKey:{} execute failed caused by:", jobKey, ex))
            .executor(executor)
            .build()
            .submit(jobScheduler::submitTask);
        JOB_MAP.put(jobKey, periodTaskHandler);

        return jobKey;
    }

    private String jobKey(NotificationJob job) {
        return new StringJoiner(SEPERATER)
            .add(job.getDeviceId())
            .add(job.getTargetIp())
            .add(String.valueOf(job.getTargetPort()))
            .toString();
    }

    @SuppressWarnings("IllegalCatch")
    private ListenableFuture<Boolean> doSchedule(NotificationJob job) {
        String deviceId = job.getDeviceId();
        String targetIp = job.getTargetIp();
        Integer targetPort = job.getTargetPort();
        String jobKey = new StringJoiner(SEPERATER)
            .add(deviceId)
            .add(targetIp)
            .add(String.valueOf(targetPort)).toString();
        job.setJobId(jobKey);

        try {
            Document document = XmlUtil.readXmlToDocument(job.getNotificationXml());
            return simluateDeviceService.sendNotification(document, deviceId, targetIp, targetPort);
        } catch (Throwable throwable) {
            LOG.warn("device:{} send notification to target:[{}:{}] error.", deviceId, targetIp, targetPort,
                throwable);

            return Futures.immediateFuture(Boolean.FALSE);
        }
    }

    @Override
    public boolean deleteJob(String jobKey) {
        PeriodTaskHandler removed = JOB_MAP.remove(jobKey);
        if (removed != null) {
            removed.stop();
            LOG.info("delet job:{} successful.", jobKey);
        }

        return removed != null;
    }

    @Override
    public Map<String, PeriodTaskHandler> allJobs() {
        return Map.copyOf(JOB_MAP);
    }
}