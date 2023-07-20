package cn.javastudy.springboot.simulator.netconf.domain;

import java.io.Serializable;
import org.eclipse.jdt.annotation.NonNull;

public final class ResultBuilder<T> {

    private static class ResultImpl<T> implements Result<T>, Serializable {
        private static final long serialVersionUID = 1L;

        private final String errorMessage;
        private final T data;
        private final boolean successful;

        ResultImpl(final boolean successful, final T data, final String errorMessage) {
            this.successful = successful;
            this.data = data;
            this.errorMessage = errorMessage;
        }

        public String errorMessage() {
            return errorMessage;
        }

        @Override
        public T getData() {
            return data;
        }

        @Override
        public boolean isSuccessful() {
            return successful;
        }

        @Override
        public String toString() {
            return "RpcResult [successful=" + successful + ", data="
                + data + ", errorMessage=" + errorMessage + "]";
        }
    }

    private String errorMsg;
    private T data;
    private final boolean successful;

    private ResultBuilder(final boolean successful, final T data) {
        this.successful = successful;
        this.data = data;
    }

    public static <T> @NonNull ResultBuilder<T> success(final T data) {
        return new ResultBuilder<>(true, data);
    }

    public static <T> @NonNull ResultBuilder<T> failed(final T data) {
        return new ResultBuilder<>(false, data);
    }

    public static <T> @NonNull ResultBuilder<T> failed(final T data, final String errorMsg) {
        return new ResultBuilder<>(false, data).withErrorMsg(errorMsg);
    }

    public static <T> @NonNull ResultBuilder<T> failed(final T data, final Throwable throwable) {
        return new ResultBuilder<>(false, data).withErrorMsg(throwable.getMessage());
    }

    public static <T> @NonNull ResultBuilder<T> status(final boolean success) {
        return new ResultBuilder<>(success, null);
    }

    @SuppressWarnings("checkstyle:hiddenField")
    public @NonNull ResultBuilder<T> withResult(final T result) {
        this.data = result;
        return this;
    }

    @SuppressWarnings("checkstyle:hiddenField")
    public @NonNull ResultBuilder<T> withErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public @NonNull Result<T> build() {
        return new ResultBuilder.ResultImpl<>(successful, data, errorMsg);
    }
}
