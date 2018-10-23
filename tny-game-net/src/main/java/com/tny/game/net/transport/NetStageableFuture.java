package com.tny.game.net.transport;

import com.tny.game.common.concurrent.StageableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2018/8/21.
 */
public class NetStageableFuture<T> extends StageableFuture<T> {

    public static <T> NetStageableFuture<T> completedFuture(T o) {
        return new NetStageableFuture<>(CompletableFuture.completedFuture(o));
    }

    public static <T> NetStageableFuture<T> createFuture() {
        return new NetStageableFuture<>(new CompletableFuture<>());
    }

    public NetStageableFuture(CompletableFuture<T> future) {
        super(future);
    }

    public NetStageableFuture() {
        super(new CompletableFuture<>());
    }

    @Override
    protected void complete(T message) {
        super.complete(message);
    }

    @Override
    protected void completeExceptionally(Throwable e) {
        super.completeExceptionally(e);
    }

    @Override
    protected void cancel() {
        super.cancel();
    }

}
