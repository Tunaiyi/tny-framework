package com.tny.game.common;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 19:11
 **/
public class CompletableFutureAide {

    public static CompletableFuture<Void> delay(Runnable runnable, long delay) {
        return CompletableFuture.runAsync(runnable, CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS));
    }

}
