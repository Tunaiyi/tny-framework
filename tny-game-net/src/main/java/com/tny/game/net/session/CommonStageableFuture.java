package com.tny.game.net.session;

import com.tny.game.common.concurrent.StageableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2018/8/21.
 */
public class CommonStageableFuture<T> extends StageableFuture<T> {

    public CommonStageableFuture(CompletableFuture<T> future) {
        super(future);
    }

    public CommonStageableFuture() {
        super(new CompletableFuture<>());
    }

    public static <T> CommonStageableFuture<T> completedFuture(T o) {
        return new CommonStageableFuture<>(CompletableFuture.completedFuture(o));
    }

    public static <T> CommonStageableFuture<T> createFuture() {
        return new CommonStageableFuture<>(new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<T> future() {
        return super.future();
    }

}
