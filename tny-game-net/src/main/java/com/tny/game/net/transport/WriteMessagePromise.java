package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-15 21:13
 */
public interface WriteMessagePromise extends WriteMessageFuture {

	void setRespondFuture(RespondFuture respondFuture);

	void success();

	<E extends Throwable> void failed(E cause);

	<E extends Throwable> void failedAndThrow(E cause) throws E;

}
