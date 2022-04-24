package com.tny.game.common.concurrent;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 2017/6/2.
 */
public interface Wait<R> {

    static <V> Wait<V> of(Future<V> future) {
        return new FutureWait<>(future);
    }

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

    R getResult();

}
