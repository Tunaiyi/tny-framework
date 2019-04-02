package com.tny.game.net.transport;

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:45
 */
public interface Transport<UID> {

    /**
     * 写出消息
     *
     * @param message 发送消息
     * @param promise  发送promise
     * @throws NetException
     */
    WriteMessageFuture write(Message<UID> message, WriteMessagePromise promise) throws NetException;

}
