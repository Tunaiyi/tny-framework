package com.tny.game.net.transport;

import com.tny.game.net.exception.NetException;
import com.tny.game.net.message.MessageSubject;


/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 18:05
 */
public interface Sender<UID> {

    /**
     * 异步发送消息
     *
     * @param subject 消息主体
     * @return 返回发送上下文
     */
    default void sendAsyn(MessageSubject subject) {
        this.sendAsyn(subject, null);
    }

    /**
     * 异步发送消息
     *
     * @param subject        消息主体
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> messageContext);

    /**
     * 同步发送消息
     *
     * @param subject 消息主体
     * @param timeout 发送超时
     * @return 返回发送上下文
     */
    default SendContext<UID> sendSync(MessageSubject subject, long timeout) throws NetException {
        return sendSync(subject, null, timeout);
    }

    /**
     * 同步发送消息
     *
     * @param subject        消息主体
     * @param messageContext 发送消息上下文
     * @param timeout        发送超时
     * @return 返回发送上下文
     */
    SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> messageContext, long timeout) throws NetException;

}
