package cn.javastudy.springboot.netconf.client.service.impl;

import cn.javastudy.springboot.netconf.client.service.NetconfNotificationService;
import cn.javastudy.springboot.netconf.client.service.NetconfServices;
import cn.javastudy.springboot.netconf.client.utils.YangParserUtils;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.opendaylight.controller.config.threadpool.ScheduledThreadPool;
import org.opendaylight.controller.config.threadpool.ThreadPool;
import org.opendaylight.controller.config.threadpool.util.FixedThreadPoolWrapper;
import org.opendaylight.controller.config.threadpool.util.ScheduledThreadPoolWrapper;
import org.opendaylight.mdsal.binding.dom.adapter.ConstantAdapterContext;
import org.opendaylight.mdsal.binding.dom.codec.api.BindingCodecTreeFactory;
import org.opendaylight.mdsal.binding.dom.codec.impl.BindingCodecContext;
import org.opendaylight.mdsal.binding.dom.codec.impl.DefaultBindingCodecTreeFactory;
import org.opendaylight.mdsal.binding.generator.impl.DefaultBindingRuntimeGenerator;
import org.opendaylight.mdsal.binding.runtime.api.BindingRuntimeGenerator;
import org.opendaylight.mdsal.binding.runtime.api.BindingRuntimeTypes;
import org.opendaylight.mdsal.binding.runtime.api.DefaultBindingRuntimeContext;
import org.opendaylight.mdsal.binding.runtime.api.ModuleInfoSnapshot;
import org.opendaylight.mdsal.binding.runtime.spi.ModuleInfoSnapshotResolver;
import org.opendaylight.mdsal.dom.api.DOMSchemaService;
import org.opendaylight.mdsal.dom.spi.FixedDOMSchemaService;
import org.opendaylight.yangtools.concepts.ObjectRegistration;
import org.opendaylight.yangtools.concepts.Registration;
import org.opendaylight.yangtools.yang.binding.YangModuleInfo;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.parser.api.YangParserFactory;
import org.opendaylight.yangtools.yang.parser.impl.DefaultYangParserFactory;
import org.opendaylight.yangtools.yang.xpath.api.YangXPathParserFactory;
import org.opendaylight.yangtools.yang.xpath.impl.AntlrXPathParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetconfServicesImpl implements NetconfServices, InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfServicesImpl.class);

    private final Set<YangModuleInfo> modelSet;

    private EventExecutor eventExecutor;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ThreadPool threadPool;
    private ScheduledThreadPool scheduledThreadPool;
    private Timer timer;

    private DefaultYangParserFactory yangParserFactory;
    private ModuleInfoSnapshotResolver snapshotResolver;
    private List<ObjectRegistration<YangModuleInfo>> modelsRegistration = new ArrayList<>();
    private ModuleInfoSnapshot moduleInfoSnapshot;
    private DOMSchemaService schemaService;
    private BindingCodecTreeFactory bindingCodecTreeFactory;
    private ConstantAdapterContext codec;

    public NetconfServicesImpl(Set<YangModuleInfo> modelSet) {
        this.modelSet = modelSet;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.eventExecutor = new DefaultEventExecutor();
        this.timer = new HashedWheelTimer();
        this.threadPool =
            new FixedThreadPoolWrapper(2, new DefaultThreadFactory("default-pool"));
        this.scheduledThreadPool =
            new ScheduledThreadPoolWrapper(2, new DefaultThreadFactory("default-scheduled-pool"));

        // INIT yang parser factory
        final YangXPathParserFactory xpathFactory = new AntlrXPathParserFactory();
        this.yangParserFactory = new DefaultYangParserFactory(xpathFactory);

        //INIT schema context
        this.snapshotResolver = new ModuleInfoSnapshotResolver("binding-dom-codec", yangParserFactory);

        EffectiveModelContext effectiveModelContext = YangParserUtils.parseYangResourceDirectory("/yang");

        this.modelsRegistration = snapshotResolver.registerModuleInfos(modelSet);
        this.moduleInfoSnapshot = snapshotResolver.takeSnapshot();
        this.schemaService = FixedDOMSchemaService.of(this.moduleInfoSnapshot, this.moduleInfoSnapshot);

        // INIT CODEC FACTORY

        final BindingRuntimeGenerator bindingRuntimeGenerator = new DefaultBindingRuntimeGenerator();
        final BindingRuntimeTypes bindingRuntimeTypes = bindingRuntimeGenerator
            .generateTypeMapping(moduleInfoSnapshot.getEffectiveModelContext());
        final DefaultBindingRuntimeContext bindingRuntimeContext
            = new DefaultBindingRuntimeContext(bindingRuntimeTypes, moduleInfoSnapshot);

        this.bindingCodecTreeFactory = new DefaultBindingCodecTreeFactory();

        final BindingCodecContext bindingCodecContext = new BindingCodecContext(bindingRuntimeContext);
        this.codec = new ConstantAdapterContext(bindingCodecContext);
    }

    @Override
    public void destroy() throws Exception {
        LOG.debug("Netconf Services destroy....");
        if (this.timer != null) {
            this.timer.stop();
        }

        if (this.threadPool != null && this.threadPool.getExecutor() != null) {
            this.threadPool.getExecutor().shutdown();
        }

        if (this.scheduledThreadPool != null && this.scheduledThreadPool.getExecutor() != null) {
            this.scheduledThreadPool.getExecutor().shutdown();
        }

        modelsRegistration.forEach(Registration::close);
        LOG.debug("Netconf Services destroy successfully....");
    }

    @Override
    public EventExecutor getEventExecutor() {
        return eventExecutor;
    }

    @Override
    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    @Override
    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    @Override
    public ThreadPool getThreadPool() {
        return threadPool;
    }

    @Override
    public ScheduledThreadPool getScheduledThreadPool() {
        return scheduledThreadPool;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public YangParserFactory getYangParserFactory() {
        return yangParserFactory;
    }

    @Override
    public ConstantAdapterContext getAdapterContext() {
        return codec;
    }

    @Override
    public NetconfNotificationService getNotificationService() {
        return null;
    }

}
