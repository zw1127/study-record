package cn.simulator.netconf.ssh;

import static java.util.Objects.requireNonNull;

import com.google.common.util.concurrent.ForwardingExecutorService;
import java.util.concurrent.ExecutorService;

/**
 * Facade for guarding against {@link #shutdown()} invocations. This is necessary as SSHD wants to shutdown the executor
 * when the server shuts down.
 */
final class ExecutorServiceFacade extends ForwardingExecutorService {
    private final ExecutorService delegate;

    ExecutorServiceFacade(final ExecutorService delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    protected ExecutorService delegate() {
        return delegate;
    }

    @Override
    public void shutdown() {
        // NO-OP
    }
}