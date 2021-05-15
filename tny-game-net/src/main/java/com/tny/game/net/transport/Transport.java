package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.util.Collection;
import java.util.function.Supplier;

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
     * @param creator 消息创建
     * @param context 发送消息
     * @throws NetException
     */
    WriteMessageFuture write(MessageCreator<UID> creator, MessageContext<UID> context) throws NetException;

    /**
     * 批量写出
     */
    void writeBatch(Supplier<Collection<Message>> messageSupplier);

}
