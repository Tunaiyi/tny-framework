package com.tny.game.net.transport;

import com.tny.game.net.exception.NetException;


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
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendContext<UID> sendAsyn(MessageContext<UID> messageContext);


    /**
     * 同步发送消息
     *
     * @param messageContext 发送消息上下文
     * @param timeout        发送超时
     * @return 返回发送上下文
     */
    SendContext<UID> sendSync(MessageContext<UID> messageContext, long timeout) throws NetException;

}
