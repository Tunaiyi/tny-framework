package com.tny.game.net.transport;

import com.tny.game.net.exception.NetException;
import com.tny.game.net.message.Message;

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
     * @param message            发送消息
     * @param context            发送上下文
     * @param waitForSendTimeout 等待发送时间(ms) <0 不等待
     * @throws NetException
     */
    SendContext<UID> write(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException;

}
