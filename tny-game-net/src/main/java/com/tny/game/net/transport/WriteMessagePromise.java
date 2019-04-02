package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-15 21:13
 */
public interface WriteMessagePromise extends WriteMessageFuture {

    void success();

    void failed(Throwable cause);

}
