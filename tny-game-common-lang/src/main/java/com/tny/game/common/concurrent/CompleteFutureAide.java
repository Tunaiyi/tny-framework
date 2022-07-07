package com.tny.game.common.concurrent;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 17:14
 **/
public final class CompleteFutureAide {

    private CompleteFutureAide() {
    }

    public static <T> CompletableFuture<T> failedFuture(Throwable cause) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(cause);
        return future;
    }

}
