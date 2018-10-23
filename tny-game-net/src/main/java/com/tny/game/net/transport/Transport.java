package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.exception.NetException;
import com.tny.game.net.message.Message;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:45
 */
public interface Transport<UID> {

    /**
     * @return 通道 Id
     */
    long getId();

    /**
     * @return 访问 Id
     */
    long getAccessId();

    /**
     * @return 属性对象
     */
    Attributes attributes();

    /**
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * @return 是否已经开启
     */
    boolean isAlive();

    /**
     * @return 获取 Tunnel 状态
     */
    TunnelState getState();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress getLocalAddress();

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
