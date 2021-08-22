package com.tny.game.net.transport;

import java.util.concurrent.Future;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-15 21:13
 */
public interface WriteMessageFuture extends Future<Void> {

	boolean isSuccess();

	Throwable cause();

}
