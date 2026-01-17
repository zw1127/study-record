package cn.javastudy.springboot.netconf.client.listener;

import cn.javastudy.springboot.netconf.client.entity.RemoteDeviceId;
import cn.javastudy.springboot.netconf.client.service.NetconfNotificationService;
import cn.javastudy.springboot.netconf.client.utils.NetconfMessageUtil;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.util.Timeout;
import io.netty.util.concurrent.Future;
import java.io.EOFException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jdt.annotation.Nullable;
import org.opendaylight.netconf.api.DocumentedException;
import org.opendaylight.netconf.api.NetconfMessage;
import org.opendaylight.netconf.api.NetconfTerminationReason;
import org.opendaylight.netconf.api.xml.XmlElement;
import org.opendaylight.netconf.api.xml.XmlNetconfConstants;
import org.opendaylight.netconf.api.xml.XmlUtil;
import org.opendaylight.netconf.client.NetconfClientDispatcher;
import org.opendaylight.netconf.client.NetconfClientSession;
import org.opendaylight.netconf.client.NetconfClientSessionListener;
import org.opendaylight.netconf.client.conf.NetconfReconnectingClientConfiguration;
import org.opendaylight.netconf.nettyutil.ReconnectFuture;
import org.opendaylight.yangtools.yang.common.ErrorSeverity;
import org.opendaylight.yangtools.yang.common.ErrorTag;
import org.opendaylight.yangtools.yang.common.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfDeviceListener implements NetconfClientSessionListener, RemoteDeviceCommunicator {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfDeviceListener.class);

    private static final VarHandle CLOSING;

    static {
        try {
            CLOSING = MethodHandles.lookup().findVarHandle(NetconfDeviceListener.class, "closing", boolean.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Lock sessionLock = new ReentrantLock();

    private final Queue<Request> requests = new ArrayDeque<>();
    private NetconfClientSession currentSession;

    private final RemoteDeviceId id;
    private final NetconfNotificationService netconfNotificationService;

    private final SettableFuture<RemoteDeviceId> firstConnectionFuture;
    private Future<?> taskFuture;

    @SuppressWarnings("unused")
    @SuppressFBWarnings(value = "UUF_UNUSED_FIELD", justification = "https://github.com/spotbugs/spotbugs/issues/2749")
    private volatile boolean closing;

    public boolean isSessionClosing() {
        return (boolean) CLOSING.getVolatile(this);
    }

    public NetconfDeviceListener(final RemoteDeviceId id,
                                 final NetconfNotificationService netconfNotificationService) {
        this.id = id;
        this.netconfNotificationService = netconfNotificationService;
        this.firstConnectionFuture = SettableFuture.create();
    }

    public ListenableFuture<RemoteDeviceId> initializeRemoteConnection(
        final NetconfClientDispatcher dispatcher, final NetconfReconnectingClientConfiguration config) {
        final ReconnectFuture reconnectFuture = dispatcher.createReconnectingClient(config);
        taskFuture = reconnectFuture;
        final Future<?> connectFuture = reconnectFuture.firstSessionFuture();

        connectFuture.addListener(future -> {
            if (!future.isSuccess() && !future.isCancelled()) {
                LOG.debug("{}: Connection failed", id, future.cause());
                if (!firstConnectionFuture.isDone()) {
                    firstConnectionFuture.setException(future.cause());
                }
            }
        });
        return firstConnectionFuture;
    }

    @Override
    public ListenableFuture<RpcResult<NetconfMessage>> sendRequest(NetconfMessage message) {
        sessionLock.lock();
        try {
            return sendRequestWithLock(message);
        } finally {
            sessionLock.unlock();
        }
    }

    private ListenableFuture<RpcResult<NetconfMessage>> sendRequestWithLock(final NetconfMessage message) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("{}: Sending message {}", id, msgToS(message));
        }

        if (currentSession == null) {
            LOG.warn("{}: Session is disconnected, failing RPC request {}", id, message);
            return Futures.immediateFuture(createSessionDownRpcResult());
        }

        final Request req = new Request(new UncancellableFuture<>(true), message);
        requests.add(req);

        currentSession.sendMessage(req.request).addListener(future -> {
            final var cause = future.cause();
            if (cause != null) {
                // We expect that a session down will occur at this point
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{}: Failed to send request {}", id, XmlUtil.toString(req.request.getDocument()), cause);
                }

                req.future.set(createErrorRpcResult(ErrorType.TRANSPORT, cause.getLocalizedMessage()));
            } else {
                LOG.trace("Finished sending request {}", req.request);
            }
        });

        return req.future;
    }

    @Override
    public void close() {
        // Cancel reconnect if in progress
        if (taskFuture != null) {
            taskFuture.cancel(false);
        }
        // Disconnect from device
        // tear down not necessary, called indirectly by the close in disconnect()
        disconnect();
    }

    private RpcResult<NetconfMessage> createSessionDownRpcResult() {
        return createErrorRpcResult(ErrorType.TRANSPORT,
            String.format("The netconf session to %1$s is disconnected", id.getName()));
    }

    private static RpcResult<NetconfMessage> createErrorRpcResult(final ErrorType errorType, final String message) {
        return RpcResultBuilder.<NetconfMessage>failed()
            .withError(errorType, ErrorTag.OPERATION_FAILED, message)
            .build();
    }

    @Override
    public void onSessionUp(NetconfClientSession session) {
        sessionLock.lock();
        try {
            LOG.debug("{}: Session established", id);
            currentSession = session;
        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public void onSessionDown(NetconfClientSession session, Exception exception) {
        // If session is already in closing, no need to call tearDown again.
        if (CLOSING.compareAndSet(this, false, true)) {
            if (exception instanceof EOFException) {
                LOG.info("{}: Session went down: {}", id, exception.getMessage());
            } else {
                LOG.warn("{}: Session went down", id, exception);
            }
            tearDown(null);
        }
    }

    @Override
    public void onSessionTerminated(NetconfClientSession session, NetconfTerminationReason reason) {
        // onSessionTerminated is called directly by disconnect, no need to compare and set isSessionClosing.
        LOG.warn("{}: Session terminated {}", id, reason);
        tearDown(reason.getErrorMessage());
    }

    public void disconnect() {
        // If session is already in closing, no need to close it again
        if (currentSession != null && CLOSING.compareAndSet(this, false, true) && currentSession.isUp()) {
            currentSession.close();
        }
    }

    private void tearDown(final String reason) {
        if (!isSessionClosing()) {
            LOG.warn("It's curious that no one to close the session but tearDown is called!");
        }
        LOG.debug("Tearing down {}", reason);
        final var futuresToCancel = new ArrayList<UncancellableFuture<RpcResult<NetconfMessage>>>();
        sessionLock.lock();
        try {
            if (currentSession != null) {
                currentSession = null;
                /*
                 * Walk all requests, check if they have been executing
                 * or cancelled and remove them from the queue.
                 */
                final var it = requests.iterator();
                while (it.hasNext()) {
                    final var r = it.next();
                    futuresToCancel.add(r.future);
                    it.remove();
                }
            }
        } finally {
            sessionLock.unlock();
        }

        // Notify pending request futures outside of the sessionLock to avoid unnecessarily blocking the caller.
        for (var future : futuresToCancel) {
            if (Strings.isNullOrEmpty(reason)) {
                future.set(createSessionDownRpcResult());
            } else {
                future.set(createErrorRpcResult(ErrorType.TRANSPORT, reason));
            }
        }

        CLOSING.setVolatile(this, false);
    }

    @Override
    public void onMessage(NetconfClientSession session, NetconfMessage message) {
        if (isNotification(message)) {
            processNotification(message);
        } else {
            processMessage(message);
        }
    }

    private @Nullable Request pollRequest() {
        sessionLock.lock();
        try {
            Request request = requests.peek();
            if (request != null) {
                request = requests.poll();
                return request;
            }
            return null;
        } finally {
            sessionLock.unlock();
        }
    }

    private void processMessage(final NetconfMessage message) {
        final @Nullable Request request = pollRequest();
        if (request == null) {
            // No matching request, bail out
            if (LOG.isWarnEnabled()) {
                LOG.warn("{}: Ignoring unsolicited message {}", id, msgToS(message));
            }
            return;
        }

        LOG.debug("{}: Message received {}", id, message);
        if (LOG.isTraceEnabled()) {
            LOG.trace("{}: Matched request: {} to response: {}", id, msgToS(request.request), msgToS(message));
        }

        final String inputMsgId = request.request.getDocument().getDocumentElement()
            .getAttribute(XmlNetconfConstants.MESSAGE_ID);
        final var outputMsgId = message.getDocument().getDocumentElement()
            .getAttribute(XmlNetconfConstants.MESSAGE_ID);
        if (!inputMsgId.equals(outputMsgId)) {
            // FIXME: we should be able to transform directly to RpcError without an intermediate exception
            final var ex = new DocumentedException("Response message contained unknown \"message-id\"", null,
                ErrorType.PROTOCOL, ErrorTag.BAD_ATTRIBUTE, ErrorSeverity.ERROR,
                ImmutableMap.of("actual-message-id", outputMsgId, "expected-message-id", inputMsgId));
            if (LOG.isWarnEnabled()) {
                LOG.warn("{}: Invalid request-reply match, reply message contains different message-id, request: {}, "
                    + "response: {}", id, msgToS(request.request), msgToS(message));
            }
            request.future.set(RpcResultBuilder.<NetconfMessage>failed().withRpcError(toRpcError(ex)).build());

            // recursively processing message to eventually find matching request
            processMessage(message);
            return;
        }

        final RpcResult<NetconfMessage> result;
        if (NetconfMessageUtil.isErrorMessage(message)) {
            // FIXME: we should be able to transform directly to RpcError without an intermediate exception
            final var ex = DocumentedException.fromXMLDocument(message.getDocument());
            if (LOG.isWarnEnabled()) {
                LOG.warn("{}: Error reply from remote device, request: {}, response: {}", id, msgToS(request.request),
                    msgToS(message));
            }
            result = RpcResultBuilder.<NetconfMessage>failed().withRpcError(toRpcError(ex)).build();
        } else {
            result = RpcResultBuilder.success(message).build();
        }

        request.future.set(result);
    }

    private static String msgToS(final NetconfMessage msg) {
        return XmlUtil.toString(msg.getDocument());
    }

    private static RpcError toRpcError(final DocumentedException ex) {
        final var errorInfo = ex.getErrorInfo();
        final String infoString;
        if (errorInfo != null) {
            final var sb = new StringBuilder();
            for (var e : errorInfo.entrySet()) {
                final var tag = e.getKey();
                sb.append('<').append(tag).append('>').append(e.getValue()).append("</").append(tag).append('>');
            }
            infoString = sb.toString();
        } else {
            infoString = "";
        }

        return ex.getErrorSeverity() == ErrorSeverity.ERROR
            ? RpcResultBuilder.newError(ex.getErrorType(), ex.getErrorTag(), ex.getLocalizedMessage(), null,
            infoString, ex.getCause())
            : RpcResultBuilder.newWarning(ex.getErrorType(), ex.getErrorTag(), ex.getLocalizedMessage(), null,
            infoString, ex.getCause());
    }

    private void processNotification(final NetconfMessage notification) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("{}: Notification received: {}", id, notification);
        }

        netconfNotificationService.dispatchNotification(id, notification);
    }

    private static boolean isNotification(final NetconfMessage message) {
        if (message.getDocument() == null) {
            // We have no message, which mean we have a FailedNetconfMessage
            return false;
        }
        final XmlElement xmle = XmlElement.fromDomDocument(message.getDocument());
        return XmlNetconfConstants.NOTIFICATION_ELEMENT_NAME.equals(xmle.getName());
    }

    private final class Request {
        final UncancellableFuture<RpcResult<NetconfMessage>> future;
        final NetconfMessage request;
        final ZonedDateTime start;
        final AtomicReference<ZonedDateTime> finishedSendMessage = new AtomicReference<>();
        Timeout timeout;

        private Request(final UncancellableFuture<RpcResult<NetconfMessage>> future,
                        final NetconfMessage request) {
            this.future = future;
            this.request = request;
            this.start = ZonedDateTime.now(TimeZone.getDefault().toZoneId());
        }

        void finishedSendTime(ZonedDateTime finishedSendTime) {
            finishedSendMessage.set(finishedSendTime);
            long cost = Duration.between(start, finishedSendTime).toMillis();
            if (LOG.isTraceEnabled()) {
                LOG.trace("{} send message deal cost:{} ms", id, cost);
            } else if (cost > 20) {
                LOG.debug("{} send message deal cost:{} ms", id, cost);
            }
        }

        ZonedDateTime finishedSendTime() {
            return finishedSendMessage.updateAndGet(value -> {
                if (value == null) {
                    LOG.warn("finishedSendTime is null.");
                    return start;
                }
                return value;
            });
        }
    }
}
