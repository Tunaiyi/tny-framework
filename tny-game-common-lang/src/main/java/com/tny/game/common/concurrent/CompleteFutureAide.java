package com.tny.game.common.concurrent;

import com.tny.game.common.utils.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 17:14
 **/
public final class CompleteFutureAide {

    private static final BiConsumer<?, ?> NOOP_COMPLETE = (_1, _2) -> {
    };

    private static final Function<Object, Object> NOOP_APPLY = ObjectAide::self;

    private static final Consumer<Object> NOOP_ACCEPT = (_1) -> {
    };

    private CompleteFutureAide() {
    }

    public static <T> CompletableFuture<T> failedFuture(Throwable cause) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(cause);
        return future;
    }

    public static <V, T extends Throwable> BiConsumer<V, ? super T> noopComplete() {
        return as(NOOP_COMPLETE);
    }

    public static <V, T extends Throwable> BiConsumer<V, ? super T> runComplete(Runnable runnable) {
        return (_1, _2) -> runnable.run();
    }

    public static <T> Function<T, T> noopApply() {
        return as(NOOP_APPLY);
    }

    public static <T> Consumer<T> noopAccept() {
        return as(NOOP_ACCEPT);
    }

}
