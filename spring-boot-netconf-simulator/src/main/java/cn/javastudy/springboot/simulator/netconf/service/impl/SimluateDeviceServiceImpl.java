package cn.javastudy.springboot.simulator.netconf.service.impl;

import static cn.javastudy.springboot.simulator.netconf.utils.Constants.DEFAULT_BASE_CAPABILITIES_WITHOUT_EXI;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;

import cn.javastudy.springboot.simulator.netconf.datastore.entity.SimulatorConfig;
import cn.javastudy.springboot.simulator.netconf.datastore.mapper.SimulatorConfigMapper;
import cn.javastudy.springboot.simulator.netconf.device.DeviceSessionManager;
import cn.javastudy.springboot.simulator.netconf.device.NetconfSimulateDevice;
import cn.javastudy.springboot.simulator.netconf.domain.CallhomeInfo;
import cn.javastudy.springboot.simulator.netconf.domain.DeviceUniqueInfo;
import cn.javastudy.springboot.simulator.netconf.domain.Result;
import cn.javastudy.springboot.simulator.netconf.domain.SimulateDeviceInfo;
import cn.javastudy.springboot.simulator.netconf.exception.SimulateException;
import cn.javastudy.springboot.simulator.netconf.monitoring.NetconfMonitoringOperationService;
import cn.javastudy.springboot.simulator.netconf.monitoring.NetconfMonitoringOperationServiceFactory;
import cn.javastudy.springboot.simulator.netconf.operate.InMemoryOperationServiceFactory;
import cn.javastudy.springboot.simulator.netconf.properties.NetconfSimulatorProperties;
import cn.javastudy.springboot.simulator.netconf.provider.DummyMonitoringService;
import cn.javastudy.springboot.simulator.netconf.provider.SimulateNegotiationFactory;
import cn.javastudy.springboot.simulator.netconf.rpchandler.RequestProcessor;
import cn.javastudy.springboot.simulator.netconf.rpchandler.RpcHandlerImpl;
import cn.javastudy.springboot.simulator.netconf.rpchandler.SettableOperationRpcProvider;
import cn.javastudy.springboot.simulator.netconf.rpchandler.impl.CreateSubscriptionRequestProcessor;
import cn.javastudy.springboot.simulator.netconf.service.NotificationMessage;
import cn.javastudy.springboot.simulator.netconf.service.SchemaContextService;
import cn.javastudy.springboot.simulator.netconf.service.SimluateDeviceService;
import cn.javastudy.springboot.simulator.netconf.service.SimulateConfigService;
import cn.javastudy.springboot.simulator.netconf.utils.Utils;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.mdsal.dom.api.DOMDataTreeWriteTransaction;
import org.opendaylight.mdsal.dom.broker.SerializedDOMDataBroker;
import org.opendaylight.mdsal.dom.spi.store.DOMStore;
import org.opendaylight.mdsal.dom.store.inmemory.InMemoryDOMDataStore;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.NetconfServerDispatcher;
import org.opendaylight.netconf.api.capability.BasicCapability;
import org.opendaylight.netconf.api.capability.Capability;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.impl.NetconfServerDispatcherImpl;
import org.opendaylight.netconf.impl.NetconfServerSession;
import org.opendaylight.netconf.impl.NetconfServerSessionNegotiatorFactory;
import org.opendaylight.netconf.impl.ServerChannelInitializer;
import org.opendaylight.netconf.impl.SessionIdProvider;
import org.opendaylight.netconf.impl.osgi.AggregatedNetconfOperationServiceFactory;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;
import org.opendaylight.netconf.shaded.sshd.common.util.threads.ThreadUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.NetconfState;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.Yang;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.netconf.state.Schemas;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.netconf.state.schemas.Schema;
import org.opendaylight.yangtools.util.ListenerRegistry;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifierWithPredicates;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeWithValue;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.LeafSetNode;
import org.opendaylight.yangtools.yang.data.api.schema.MapEntryNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.SystemMapNode;
import org.opendaylight.yangtools.yang.data.api.schema.builder.CollectionNodeBuilder;
import org.opendaylight.yangtools.yang.data.impl.schema.Builders;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNodes;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

@Service
public class SimluateDeviceServiceImpl implements SimluateDeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(SimluateDeviceServiceImpl.class);

    private final Map<String, NetconfSimulateDevice> startedDeviceMap = new ConcurrentHashMap<>();

    @Resource
    private SchemaContextService schemaContextService;

    @Resource
    private NetconfSimulatorProperties properties;

    @Resource
    private SimulatorConfigMapper simulatorConfigMapper;

    @Resource
    private SimulateConfigService simulateConfigService;

    private NioEventLoopGroup nettyThreadgroup;
    private HashedWheelTimer hashedWheelTimer;
    private ListeningScheduledExecutorService scheduledExecutorService;
    private ExecutorService nioExecutor;
    private AsynchronousChannelGroup channelGroup;

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    @PreDestroy
    public void stop() {

    }

    private void initThreadPool() {
        LOG.info("starting init thread pool.");
        this.nettyThreadgroup = new NioEventLoopGroup();
        this.hashedWheelTimer = new HashedWheelTimer();
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("simulator-init-data-save-%d")
            .build();
        scheduledExecutorService = listeningDecorator(Executors.newScheduledThreadPool(10, threadFactory));

        int threadPoolSize = properties.getThreadPoolSize();
        LOG.info("NetconfSimulatorProperties threadsize:{}", threadPoolSize);
        nioExecutor = ThreadUtils.newFixedThreadPool("netconf-ssh-server-nio-group", threadPoolSize);
        try {
            channelGroup = AsynchronousChannelGroup.withThreadPool(nioExecutor);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create group.", e);
        }
        LOG.info("init thread pool successful.");
    }

    @Override
    public ListenableFuture<Result<SimulateDeviceInfo>> startSimulateDevice(SimulateDeviceInfo deviceInfo) {
        LOG.info("start simulate device:{} ", deviceInfo);
        AtomicBoolean initFlag = new AtomicBoolean(true);
        DeviceSessionManager sessionManager = new DeviceSessionManager(deviceInfo);
        NetconfServerDispatcher serverDispatcher = createDispatcher(deviceInfo, sessionManager, initFlag);

        NetconfSimulateDevice simulateDevice =
            new NetconfSimulateDevice(serverDispatcher, nettyThreadgroup, channelGroup, deviceInfo, sessionManager);
        ListenableFuture<Result<SimulateDeviceInfo>> future = simulateDevice.start();

        future.addListener(() -> initFlag.set(false), MoreExecutors.directExecutor());
        startedDeviceMap.put(deviceInfo.getUniqueKey(), simulateDevice);

        return future;
    }

    @Override
    public void stopSimulateDevice(String uniqueKey) {
        NetconfSimulateDevice simulateDevice = startedDeviceMap.remove(uniqueKey);
        if (simulateDevice != null) {
            simulateDevice.stop();
        }
    }

    @Override
    public ListenableFuture<Result<CallhomeInfo>> callhomeConnect(String uniqueKey,
                                                                  String callhomeIp,
                                                                  Integer callhomePort) {
        NetconfSimulateDevice simulateDevice = startedDeviceMap.get(uniqueKey);
        if (simulateDevice == null) {
            LOG.warn("device:{} does not started.", uniqueKey);
            return Futures.immediateFailedFuture(new RuntimeException(uniqueKey + " does not started."));
        }
        return simulateDevice.callhomeConnect(callhomeIp, callhomePort);
    }

    @Override
    public void callhomeDisconnect(String uniqueKey, String callhomeIp, Integer callhomePort) {
        NetconfSimulateDevice simulateDevice = startedDeviceMap.get(uniqueKey);
        if (simulateDevice == null) {
            LOG.warn("device:{} does not started.", uniqueKey);
            return;
        }
        simulateDevice.callhomeDisconnect(callhomeIp, callhomePort);
    }

    @Override
    public Map<String, NetconfSimulateDevice> startedDevices() {
        return Map.copyOf(startedDeviceMap);
    }

    @Override
    public ListenableFuture<Boolean> sendNotification(Document notificationContent,
                                                      String deviceId,
                                                      String targetIp,
                                                      Integer targetPort) {
        NotificationMessage notification = NotificationMessage.ofNotificationContent(notificationContent);

        NetconfSimulateDevice simulateDevice = startedDeviceMap.get(deviceId);
        if (simulateDevice == null) {
            LOG.warn("device:{} does not exist.", deviceId);
            return Futures.immediateFailedFuture(new SimulateException("device:{0} does not exist.", deviceId));
        }

        Map<InetSocketAddress, NetconfServerSession> sessions = simulateDevice.getSessionManager().getSessions();
        if (CollectionUtils.isEmpty(sessions)) {
            LOG.warn("device:{} connected session is null.", deviceId);
            return Futures.immediateFailedFuture(
                new SimulateException("device:{0} connected session is null.", deviceId));
        }

        InetSocketAddress inetAddress = Utils.getInetAddress(targetIp, targetPort.toString());
        NetconfServerSession session = sessions.get(inetAddress);
        if (session == null) {
            LOG.warn("targetIp:{} targetPort:{} session does not exist.", targetIp, targetPort);
            return Futures.immediateFailedFuture(
                new SimulateException("targetIp:{0} targetPort:{1} session does not exist.", deviceId, targetPort));
        }

        SettableFuture<Boolean> result = SettableFuture.create();
        ChannelFuture channelFuture = session.sendMessage(notification);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                result.set(Boolean.TRUE);
                LOG.info("device: {} send notification:{} to target: {}:{} successful.",
                    deviceId, notification, targetIp, targetPort);
            } else {
                result.set(Boolean.FALSE);
            }
        });

        return Futures.withTimeout(result, 120, TimeUnit.SECONDS, scheduledExecutorService);
    }

    private NetconfServerDispatcher createDispatcher(DeviceUniqueInfo uniqueInfo,
                                                     DeviceSessionManager sessionManager,
                                                     AtomicBoolean initFlag) {
        final SessionIdProvider idProvider = new SessionIdProvider();

        Set<Capability> capabilities = new HashSet<>(schemaContextService.supportedCapabilities());
        capabilities.add(new BasicCapability("urn:ietf:params:netconf:capability:candidate:1.0"));
        if (properties.isRunningWritable()) {
            capabilities.add(new BasicCapability("urn:ietf:params:netconf:capability:writable-running:1.0"));
        }

        DummyMonitoringService monitoringService = new DummyMonitoringService(capabilities, sessionManager);

        NetconfOperationServiceFactory aggregatedFactory = createOperationServiceFactory(
            capabilities, monitoringService, uniqueInfo, initFlag);

        NetconfServerSessionNegotiatorFactory serverNegotiatorFactory = new SimulateNegotiationFactory(
            hashedWheelTimer, aggregatedFactory, idProvider,
            properties.getGenerateConfigsTimeout(), monitoringService, DEFAULT_BASE_CAPABILITIES_WITHOUT_EXI);

        ServerChannelInitializer serverChannelInitializer = new ServerChannelInitializer(serverNegotiatorFactory);
        return new NetconfServerDispatcherImpl(serverChannelInitializer, nettyThreadgroup, nettyThreadgroup);
    }

    private NetconfOperationServiceFactory createOperationServiceFactory(Set<Capability> capabilities,
                                                                         DummyMonitoringService monitoringService,
                                                                         DeviceUniqueInfo uniqueInfo,
                                                                         AtomicBoolean initFlag) {
        DOMDataBroker domDataBroker = createDomDataBroker(uniqueInfo, initFlag);
        initConfigData(monitoringService, uniqueInfo, domDataBroker);

        AggregatedNetconfOperationServiceFactory aggregatedFactory = new AggregatedNetconfOperationServiceFactory();

        NetconfMonitoringOperationServiceFactory monitoringServiceFactory =
            new NetconfMonitoringOperationServiceFactory(new NetconfMonitoringOperationService(monitoringService));

        InMemoryOperationServiceFactory operationServiceFactory =
            new InMemoryOperationServiceFactory(capabilities, schemaContextService, domDataBroker);

        aggregatedFactory.onAddNetconfOperationServiceFactory(operationServiceFactory);
        aggregatedFactory.onAddNetconfOperationServiceFactory(monitoringServiceFactory);

        Map<QName, RequestProcessor> rpcProcessors = new HashMap<>();

        CreateSubscriptionRequestProcessor createSubscription = new CreateSubscriptionRequestProcessor();
        rpcProcessors.put(createSubscription.getIdentifier(), createSubscription);

        //后续支持其它的RPC可以加在这里
        RpcHandlerImpl rpcHandler = new RpcHandlerImpl(schemaContextService, rpcProcessors);
        SettableOperationRpcProvider settableService = new SettableOperationRpcProvider(rpcHandler);
        aggregatedFactory.onAddNetconfOperationServiceFactory(settableService);

        return aggregatedFactory;
    }

    @SuppressWarnings("IllegalCatch")
    private void prepareSchemasForNetconfMonitoring(DummyMonitoringService netconfMonitor,
                                                    String deviceId,
                                                    DOMDataTreeWriteTransaction writeTransaction) {
        try {
            ContainerNode netconf = createNetconfState(netconfMonitor);
            YangInstanceIdentifier yangInstanceIdentifier = YangInstanceIdentifier.builder()
                .node(NetconfState.QNAME)
                .build();

            writeTransaction.put(LogicalDatastoreType.OPERATIONAL, yangInstanceIdentifier, netconf);
        } catch (Throwable ex) {
            LOG.error("initia device:{} SchemasForNetconfMonitoring failed.", deviceId, ex);
        }
    }

    private ContainerNode createNetconfState(DummyMonitoringService monitor) {
        final QName identifier = QName.create(Schema.QNAME, "identifier");
        final QName version = QName.create(Schema.QNAME, "version");
        final QName format = QName.create(Schema.QNAME, "format");
        final QName location = QName.create(Schema.QNAME, "location");
        final QName namespace = QName.create(Schema.QNAME, "namespace");

        CollectionNodeBuilder<MapEntryNode, SystemMapNode> schemaMapEntryNodeMapNodeCollectionNodeBuilder =
            Builders.mapBuilder().withNodeIdentifier(new NodeIdentifier(Schema.QNAME));
        LeafSetNode<String> locationLeafSet = Builders.<String>leafSetBuilder()
            .withNodeIdentifier(new NodeIdentifier(location))
            .withChild(Builders.<String>leafSetEntryBuilder()
                .withNodeIdentifier(new NodeWithValue<>(location, "NETCONF"))
                .withValue("NETCONF")
                .build())
            .build();

        Map<QName, Object> keyValues = new HashMap<>();
        for (final Schema schema : monitor.getSchemas().nonnullSchema().values()) {
            keyValues.put(identifier, schema.getIdentifier());
            keyValues.put(version, schema.getVersion());
            keyValues.put(format, Yang.QNAME);

            schemaMapEntryNodeMapNodeCollectionNodeBuilder.withChild(Builders.mapEntryBuilder()
                .withNodeIdentifier(NodeIdentifierWithPredicates.of(Schema.QNAME, keyValues))
                .withChild(ImmutableNodes.leafNode(identifier, schema.getIdentifier()))
                .withChild(ImmutableNodes.leafNode(version, schema.getVersion()))
                .withChild(ImmutableNodes.leafNode(format, Yang.QNAME))
                .withChild(ImmutableNodes.leafNode(namespace, schema.getNamespace().getValue()))
                .withChild(locationLeafSet)
                .build());
        }

        return Builders.containerBuilder()
            .withNodeIdentifier(new NodeIdentifier(NetconfState.QNAME))
            .withChild(Builders.containerBuilder()
                .withNodeIdentifier(new NodeIdentifier(Schemas.QNAME))
                .withChild(schemaMapEntryNodeMapNodeCollectionNodeBuilder.build())
                .build())
            .build();
    }

    private void initConfigData(DummyMonitoringService netconfMonitor,
                                DeviceUniqueInfo uniqueInfo, DOMDataBroker domDataBroker) {
        DOMDataTreeWriteTransaction writeTransaction = domDataBroker.newWriteOnlyTransaction();

        String uniqueKey = uniqueInfo.getUniqueKey();
        prepareSchemasForNetconfMonitoring(netconfMonitor, uniqueKey, writeTransaction);

        List<SimulatorConfig> simulatorConfigs = simulatorConfigMapper.selectByDeviceId(uniqueKey);
        try {
            // 数据库中没有数据，则需要从模板中取数据
            if (CollectionUtils.isEmpty(simulatorConfigs)) {
                String initialXml = simulateConfigService.initialConfigXml(uniqueInfo);

                XmlElement xmlElement = XmlElement.fromString(initialXml);
                List<XmlElement> xmlElementList = xmlElement.getChildElements();
                if (CollectionUtils.isEmpty(xmlElementList)) {
                    LOG.warn("config template is empty.");
                    return;
                }

                xmlElementList.forEach(element -> saveDatabroker(element, uniqueKey, writeTransaction));
            } else {
                for (SimulatorConfig simulatorConfig : simulatorConfigs) {
                    saveSimulatorConfig(simulatorConfig, writeTransaction);
                }
            }
        } catch (DocumentedException e) {
            LOG.warn("read xml to element error.", e);
            writeTransaction.cancel();
        } finally {
            writeTransaction.commit()
                .withTimeout(60, TimeUnit.SECONDS, scheduledExecutorService)
                .addCallback(new FutureCallback<CommitInfo>() {
                    @Override
                    public void onSuccess(CommitInfo result) {
                        LOG.info("init device:{} config data successful.", uniqueInfo);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOG.info("init device:{} config data failed.", uniqueInfo, throwable);
                    }
                }, MoreExecutors.directExecutor());
        }
    }

    @SuppressWarnings("IllegalCatch")
    private void saveSimulatorConfig(SimulatorConfig simulatorConfig,
                                     DOMDataTreeWriteTransaction writeTransaction) throws DocumentedException {
        try {
            String nodeValue = simulatorConfig.getNodeValue();
            if (StringUtils.isEmpty(nodeValue)) {
                LOG.warn("SimulatorConfig:{} nodeValue is empty.", simulatorConfig);
                return;
            }

            XmlElement xmlElement = XmlElement.fromString(nodeValue);
            saveDatabroker(xmlElement, simulatorConfig.getDeviceId(), writeTransaction);

            //同时也要更新数据库中的数据
            simulateConfigService.saveToDb(simulatorConfig.getDeviceId(), xmlElement);
        } catch (Throwable ex) {
            LOG.error("saveSimulator：{} Config failed", simulatorConfig.getDeviceId(), ex);
        }
    }

    @SuppressWarnings("IllegalCatch")
    public void saveDatabroker(XmlElement xmlElement, String deviceId, DOMDataTreeWriteTransaction writeTransaction) {
        try {
            NormalizedNode normalizedNode = schemaContextService.parseToNormalizedNode(xmlElement);
            YangInstanceIdentifier identifier = YangInstanceIdentifier.builder()
                .node(schemaContextService.findQName(xmlElement))
                .build();
//            writeTransaction.put(LogicalDatastoreType.CONFIGURATION, identifier, normalizedNode);
            //目前get 和 get-config 都从Operational datastore中读配置数据
            writeTransaction.put(LogicalDatastoreType.OPERATIONAL, identifier, normalizedNode);
        } catch (Throwable ex) {
            LOG.error("initia device:{} xmlElement name:{} failed.", deviceId, xmlElement.getName(), ex);
        }
    }

    private DOMDataBroker createDomDataBroker(DeviceUniqueInfo uniqueInfo, AtomicBoolean initFlag) {
        String uniqueSign = uniqueInfo.getUniqueKey();
        ListenerRegistry<EffectiveModelContextListener> listeners = ListenerRegistry.create();

//        InMemoryDOMDataStore cfgStore = new InMemoryDOMDataStore("CFG-" + uniqueSign,
//            LogicalDatastoreType.CONFIGURATION,
//            getDataTreeChangeListenerExecutor(),
//            InMemoryDOMDataStoreConfigProperties.DEFAULT_MAX_DATA_CHANGE_LISTENER_QUEUE_SIZE, false);
//        cfgStore.registerTreeChangeListener(YangInstanceIdentifier.create(), new)
//        listeners.register(cfgStore);

        InMemoryDOMDataStore optStore =
            new InMemoryDOMDataStore("OPER" + uniqueSign, getDataTreeChangeListenerExecutor());
        listeners.register(optStore);

        listeners.streamListeners().forEach(listener ->
            listener.onModelContextUpdated(schemaContextService.getSchemaContext()));

        SimulateDOMDataTreeChangeListener dataTreeChangeListener =
            new SimulateDOMDataTreeChangeListener(uniqueInfo, initFlag, schemaContextService, simulateConfigService);
        optStore.registerTreeChangeListener(YangInstanceIdentifier.create(), dataTreeChangeListener);

        Map<LogicalDatastoreType, DOMStore> datastores = ImmutableMap.<LogicalDatastoreType, DOMStore>builder()
            .put(LogicalDatastoreType.OPERATIONAL, optStore)
//            .put(LogicalDatastoreType.CONFIGURATION, cfgStore)
            .build();

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("datastore-" + uniqueSign + "-%d")
            .build();
        return new SerializedDOMDataBroker(datastores,
            listeningDecorator(Executors.newSingleThreadExecutor(threadFactory)));
    }


    private ListeningExecutorService getDataTreeChangeListenerExecutor() {
        return MoreExecutors.newDirectExecutorService();
    }
}
