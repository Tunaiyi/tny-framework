package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
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
     * @param promise 发送promise
     * @throws NetException
     */
    WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException;

    /**
     * 写出消息
     *
     * @param maker   消息创建
     * @param context 发送消息
     * @throws NetException
     */
    WriteMessageFuture write(MessageMaker<UID> maker, MessageContext<UID> context) throws NetException;

    /**
     * 批量写出
     */
    void write(MessagesCollector collector);

    /**
     * 创建写出Promise
     *
     * @param sendTimeout 写出超时
     * @return 返回Promise
     */
    WriteMessagePromise createWritePromise(long sendTimeout);

}
