package com.tny.game.net.transport;

import com.tny.game.net.message.Message;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-19 19:21
 */
public interface WriteCallback<UID> {

    /**
     * 发送完成
     *
     * @param message 消息
     * @param context 消息上下文
     */
    void onWrite(boolean success, Throwable cause, Message<UID> message, MessageContext<UID> context);

}
