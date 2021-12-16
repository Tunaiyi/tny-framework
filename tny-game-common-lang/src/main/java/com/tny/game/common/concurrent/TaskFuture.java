package com.tny.game.common.concurrent;

import java.util.concurrent.Future;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/16 3:25 PM
 */
public interface TaskFuture<V> extends Future<V> {

	void success(V value);

	void failure(Throwable throwable);

}
