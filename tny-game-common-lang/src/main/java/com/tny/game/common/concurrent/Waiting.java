package com.tny.game.common.concurrent;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 2017/6/2.
 */
public interface Waiting<R> {

	static <V> Waiting<V> of(Future<V> future) {
		return new FutureWaiter<>(future);
	}

	boolean isDone();

	boolean isFailed();

	boolean isSuccess();

	Throwable getCause();

	R getResult();

}
