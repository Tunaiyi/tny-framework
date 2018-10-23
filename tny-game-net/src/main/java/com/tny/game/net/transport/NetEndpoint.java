package com.tny.game.net.transport;

import com.tny.game.net.exception.NetException;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-09 19:21
 */
public interface NetEndpoint<UID> extends Endpoint<UID> {

    /**
     * 处理收到消息
     *
     * @param tunnel  通道
     * @param message 消息
     */
    boolean receive(Tunnel<UID> tunnel, Message<UID> message);

    /**
     * 异步发送消息
     *
     * @param tunnel         发送的通道
     * @param subject        消息主体
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendContext<UID> sendAsyn(Tunnel<UID> tunnel, MessageSubject subject, MessageContext<UID> messageContext);

    /**
     * 同步发送消息
     *
     * @param tunnel         发送的通道
     * @param subject        消息主体
     * @param messageContext 发送消息上下文
     * @param timeout        发送超时
     * @return 返回发送上下文
     */
    SendContext<UID> sendSync(Tunnel<UID> tunnel, MessageSubject subject, MessageContext<UID> messageContext, long timeout) throws NetException;

    /**
     * 通道销毁
     *
     * @param tunnel 销毁通道
     */
    void onDisable(NetTunnel<UID> tunnel);

}
