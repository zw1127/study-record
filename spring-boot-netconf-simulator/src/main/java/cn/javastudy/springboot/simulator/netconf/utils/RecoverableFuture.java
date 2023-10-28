package cn.javastudy.springboot.simulator.netconf.utils;

import static java.util.Objects.requireNonNull;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 类似于 transform 的 future，提供了能够指定匹配一些异常并从异常状态恢复成正常状态；
 * 应用场景：一些在 future 异常时，仍然希望按正常处理，统一用 future 的 transform 处理的场景。
 * 比如：
 * {@code
 * <br/> SettableFuture<Void> result = SettableFuture.create();
 * <br/> ListenableFuture<Void> from = xxx;
 * <br/> Futures.addCallback(from, new FutureCallback<Void>() {
 * <br/>     @Override
 * <br/>     public void onSuccess(@Nullable Void v) {
 * <br/>         result.set(v);
 * <br/>     }
 * <br/>
 * <br/>     @Override
 * <br/>     public void onFailure(Throwable throwable) {
 * <br/>         if (throwable instanceof IllegalStateException) {
 * <br/>             result.set(null);
 * <br/>         } else {
 * <br/>             result.setException(throwable);
 * <br/>         }
 * <br/>     }
 * <br/> }, MoreExecutors.directExecutor());
 * <br/> result.addListener(() -> from.cancel(true), MoreExecutors.directExecutor());
 * <br/> Futures.transform(from, null -> true, MoreExecutors.directExecutor());
 * <br/>
 * }
 * 这里的 `cancel(true)` 仍然不能传递上层调用时的入参，通过这个工具类可以传递，并且代码可以简化为：
 * {@code
 * <br/> ListenableFuture<Void> from = xxx;
 * <br/> RecoverableFuture.resumeOnError(from, ex -> {
 * <br/>     if (throwable instanceof IllegalStateException) {
 * <br/>         return Futures.immediateFuture(null);
 * <br/>     } else {
 * <br/>         return Futures.immediateFailedFuture(ex);
 * <br/>     }
 * <br/> }, MoreExecutors.directExecutor());
 * <br/>
 * }
 */
public class RecoverableFuture<V> extends AbstractFuture<V> implements Runnable {

    /**
     * 把输入的 future 转换为一个可恢复的 future 对象。
     *
     * @param input    源 future
     * @param resumer  当 input 参数的 future 异常结束时会调用并传入 input 参数产生的异常；
     *                 调用者需要根据这个异常判断是否需要恢复成正常的状态；
     *                 返回一个新的 future 对象；如果这个返回的对象是正常的或者是 null，
     *                 都会使 input 参数的 future 产生的异常被截断掉，本方法返回的这个异常将是正常状态，
     *                 future 中可以得到的返回值来源于 resumer 返回的 future 中；
     *                 如果 resumer 返回了 null，相当于返回 Futures.immediateFuture(null)，即最终得到的
     *                 返回值也是 null；
     *                 如果 resumer 过程产生任何异常，这个异常会被封装到返回的 future 里（而不是封装 input 参数的那个异常）。
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    public static <V> FluentFuture<V> resumeOnError(
        ListenableFuture<V> input,
        Function<Throwable, ? extends ListenableFuture<V>> resumer,
        Executor executor) {
        RecoverableFuture<V> future = new RecoverableFuture<V>(input, resumer);
        input.addListener(future, executor);
        return FluentFuture.from(future);
    }

    public static <V, U> FluentFuture<U> resumeOnError(
        ListenableFuture<V> input,
        Function<V, U> onSuccess,
        Function<Throwable, ? extends ListenableFuture<U>> onError,
        Executor executor) {
        ListenableFuture<U> transformed = Futures.transform(input, onSuccess::apply, MoreExecutors.directExecutor());
        RecoverableFuture<U> future = new RecoverableFuture<U>(transformed, onError);
        input.addListener(future, executor);
        return FluentFuture.from(future);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会得到 null 值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    public static <V> FluentFuture<V> alwaysResume(
        ListenableFuture<V> input, Executor executor) {
        return resumeOnError(input, ex -> null, executor);
    }

    public static <V, U> FluentFuture<U> alwaysResume(
        ListenableFuture<V> input, Function<V, U> onSucess, Executor executor) {
        return resumeOnError(input, onSucess, ex -> null, executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会得到 null 值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param consumer 消费者，在 input 的 future 失败时被调用，消费期间产生任何异常也不影响最终结果
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    @SuppressWarnings("IllegalCatch")
    public static <V> FluentFuture<V> alwaysResume(
        ListenableFuture<V> input, Consumer<Throwable> consumer, Executor executor) {
        return resumeOnError(
            input,
            ex -> {
                if (null != consumer) {
                    try {
                        consumer.accept(ex);
                    } catch (Throwable ex1) {
                        return null;
                    }
                }
                return null;
            },
            executor);
    }

    @SuppressWarnings("IllegalCatch")
    public static <V, U> FluentFuture<U> alwaysResume(
        ListenableFuture<V> input, Function<V, U> onSuccess, Consumer<Throwable> consumer, Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> {
                if (null != consumer) {
                    try {
                        consumer.accept(ex);
                    } catch (Throwable ex1) {
                        return null;
                    }
                }
                return null;
            },
            executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会得到 fallback 参数指定的值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param fallback 当 input 参数的 future 异常时，会把这个值传递到返回的 future 里面
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    public static <V> FluentFuture<V> alwaysResume(
        ListenableFuture<V> input, V fallback, Executor executor) {
        return resumeOnError(
            input,
            ex -> Futures.immediateFuture(fallback),
            executor);
    }

    public static <V, U> FluentFuture<U> alwaysResume(
        ListenableFuture<V> input, Function<V, U> onSuccess, U fallback, Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> Futures.immediateFuture(fallback),
            executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会通过 fallback 得到要返回的值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param fallback 当 input 参数的 future 异常时，会调用这个回调函数获取值传递到返回的 future 里面；调用异常时，会返回 null
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    @SuppressWarnings("IllegalCatch")
    public static <V> FluentFuture<V> alwaysResumeAndGet(
        ListenableFuture<V> input, Supplier<V> fallback, Executor executor) {
        return resumeOnError(
            input,
            ex -> {
                try {
                    return Futures.immediateFuture(fallback.get());
                } catch (Throwable ex1) {
                    return Futures.immediateFuture(null);
                }
            },
            executor);
    }

    @SuppressWarnings("IllegalCatch")
    public static <V, U> FluentFuture<U> alwaysResumeAndGet(
        ListenableFuture<V> input, Function<V, U> onSuccess, Supplier<U> fallback, Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> {
                try {
                    return Futures.immediateFuture(fallback.get());
                } catch (Throwable ex1) {
                    return Futures.immediateFuture(null);
                }
            },
            executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会通过 fallback 得到要返回的值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param resumer  当 input 参数的 future 异常时，会使用异常对象作为参数调用这个回调函数获取值传递到返回的 future 里面；调用异常时，会返回 null
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    @SuppressWarnings("IllegalCatch")
    public static <V> FluentFuture<V> alwaysResumeAndCalculate(
        ListenableFuture<V> input, Function<Throwable, V> resumer, Executor executor) {
        return resumeOnError(
            input,
            ex -> {
                try {
                    return Futures.immediateFuture(resumer.apply(ex));
                } catch (Throwable ex1) {
                    return Futures.immediateFuture(null);
                }
            },
            executor);
    }

    @SuppressWarnings("IllegalCatch")
    public static <V, U> FluentFuture<U> alwaysResumeAndCalculate(
        ListenableFuture<V> input, Function<V, U> onSuccess, Function<Throwable, U> resumer, Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> {
                try {
                    return Futures.immediateFuture(resumer.apply(ex));
                } catch (Throwable ex1) {
                    return Futures.immediateFuture(null);
                }
            },
            executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会得到 fallback 参数指定的值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param fallback 当 input 参数的 future 异常时，会调用这个回调函数获取值传递到返回的 future 里面；调用异常时，会返回 null
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    public static <V> FluentFuture<V> alwaysResumeAsync(
        ListenableFuture<V> input, ListenableFuture<V> fallback, Executor executor) {
        return resumeOnError(
            input,
            ex -> fallback,
            executor);
    }

    public static <V, U> FluentFuture<U> alwaysResumeAsync(
        ListenableFuture<V> input, Function<V, U> onSuccess, ListenableFuture<U> fallback, Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> fallback,
            executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会通过 fallback 参数得到要返回的值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param fallback 当 input 参数的 future 异常时，会调用这个回调函数获取值传递到返回的 future 里面；调用异常时，会返回 null
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    @SuppressWarnings("IllegalCatch")
    public static <V> FluentFuture<V> alwaysResumeAsyncGet(
        ListenableFuture<V> input, Supplier<ListenableFuture<V>> fallback, Executor executor) {
        return resumeOnError(
            input,
            ex -> {
                try {
                    return fallback.get();
                } catch (Throwable ex1) {
                    return null;
                }
            },
            executor);
    }

    @SuppressWarnings("IllegalCatch")
    public static <V, U> FluentFuture<U> alwaysResumeAsyncGet(
        ListenableFuture<V> input, Function<V, U> onSuccess, Supplier<ListenableFuture<U>> fallback,
        Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> {
                try {
                    return fallback.get();
                } catch (Throwable ex1) {
                    return null;
                }
            },
            executor);
    }

    /**
     * 把输入的 future 转换为一个总是恢复正常的 future 对象；这个返回的 future 永远不会异常失败；
     * 如果 input 的 future 异常了，这个返回的 future 会通过 fallback 参数得到要返回的值；
     * 如果 input 的 future 正常结束，其结果会被传递到返回的 future 中。
     *
     * @param input    源 future
     * @param resumer  当 input 参数的 future 异常时，会把异常作为参数调用这个回调函数获取值传递到返回的 future 里面；调用异常时，会返回 null
     * @param executor executor
     * @param <V>      返回值类型
     * @return 返回的新 future
     */
    @SuppressWarnings("IllegalCatch")
    public static <V> FluentFuture<V> alwaysResumeAsyncCalculate(
        ListenableFuture<V> input, Function<Throwable, ListenableFuture<V>> resumer, Executor executor) {
        return resumeOnError(
            input,
            ex -> {
                try {
                    return resumer.apply(ex);
                } catch (Throwable ex1) {
                    return null;
                }
            },
            executor);
    }

    @SuppressWarnings("IllegalCatch")
    public static <V, U> FluentFuture<U> alwaysResumeAsyncCalculate(
        ListenableFuture<V> input, Function<V, U> onSuccess, Function<Throwable, ListenableFuture<U>> resumer,
        Executor executor) {
        return resumeOnError(
            input,
            onSuccess,
            ex -> {
                try {
                    return resumer.apply(ex);
                } catch (Throwable ex1) {
                    return null;
                }
            },
            executor);
    }

    @Nullable
    protected ListenableFuture<V> inputFuture;

    @Nullable
    protected final Function<Throwable, ? extends ListenableFuture<V>> resumer;

    protected RecoverableFuture(ListenableFuture<V> inputFuture,
                                Function<Throwable, ? extends ListenableFuture<V>> resumer) {
        this.inputFuture = requireNonNull(inputFuture);
        this.resumer = requireNonNull(resumer);
    }

    @SuppressWarnings("IllegalCatch")
    public final void run() {
        // input future is done
        ListenableFuture<? extends V> localInputFuture = this.inputFuture;
        if (localInputFuture != null && !this.isCancelled()) {
            this.inputFuture = null;
            V sourceResult = null;
            Throwable throwable = null;

            try {
                sourceResult = Futures.getDone(localInputFuture);
            } catch (ExecutionException executionException) {
                throwable = requireNonNull(executionException.getCause());
            } catch (Throwable ex) {
                throwable = ex;
            }

            if (throwable instanceof ExecutionException) {
                throwable = throwable.getCause();
            }
            if (throwable == null) {
                this.set(sourceResult);
            } else {
                try {
                    if (null != this.resumer) {
                        ListenableFuture<? extends V> resumed = this.resumer.apply(throwable);
                        if (null != resumed) {
                            this.setFuture(resumed);
                        } else {
                            this.set(null);
                        }
                    } else {
                        this.setException(throwable);
                    }
                } catch (Throwable ex1) {
                    this.setException(ex1);
                }
            }
        }
    }

    protected String pendingToString() {
        ListenableFuture<? extends V> localInputFuture = this.inputFuture;
        String superString = super.pendingToString();
        String resultString = "";
        if (localInputFuture != null) {
            resultString = "inputFuture=[" + localInputFuture + "], ";
        }

        if (resumer != null) {
            return resultString + "exceptionResumer=[" + resumer + "]";
        } else {
            return superString != null ? resultString + superString : null;
        }
    }

    @Override
    protected final void afterDone() {
        if (this.isCancelled() && null != this.inputFuture && !this.inputFuture.isDone()) {
            this.inputFuture.cancel(this.wasInterrupted());
        }
        this.inputFuture = null;
    }

}
