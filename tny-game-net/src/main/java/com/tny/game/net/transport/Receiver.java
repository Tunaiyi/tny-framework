package com.tny.game.net.transport;

import com.tny.game.net.message.Message;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 18:05
 */
public interface Receiver<UID> {

    /**
     * 接收消息
     *
     * @param message 消息
     */
    boolean receive(Message<UID> message);

}
