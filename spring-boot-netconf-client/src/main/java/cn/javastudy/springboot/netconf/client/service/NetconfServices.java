package cn.javastudy.springboot.netconf.client.service;

import io.netty.channel.EventLoopGroup;
import io.netty.util.Timer;
import io.netty.util.concurrent.EventExecutor;
import org.opendaylight.controller.config.threadpool.ScheduledThreadPool;
import org.opendaylight.controller.config.threadpool.ThreadPool;
import org.opendaylight.mdsal.binding.dom.adapter.ConstantAdapterContext;
import org.opendaylight.yangtools.yang.parser.api.YangParserFactory;

public interface NetconfServices {

    EventExecutor getEventExecutor();

    EventLoopGroup getBossGroup();

    EventLoopGroup getWorkerGroup();

    ThreadPool getThreadPool();

    ScheduledThreadPool getScheduledThreadPool();

    Timer getTimer();

    YangParserFactory getYangParserFactory();

    ConstantAdapterContext getAdapterContext();

    NetconfNotificationService getNotificationService();
}
