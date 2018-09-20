package com.tny.game.common.concurrent;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 2017/6/2.
 */
public interface Waiter<R> {

    static <V> Waiter<V> of(Future<V> future) {
        return new FutureWaiter<>(future);
    }

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

    R getResult();

}
