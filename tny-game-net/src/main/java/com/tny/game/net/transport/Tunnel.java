package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.endpoint.Endpoint;
import com.tny.game.net.message.Message;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Netter<UID>, Transport<UID>, Sender<UID>, Receiver<UID> {

    /**
     * @return 通道 Id
     */
    long getId();

    /**
     * @return 访问 Id
     */
    long getAccessId();

    /**
     * @return 终端模式
     */
    TunnelMode getMode();

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
     * @return 获取绑定中断
     */
    Optional<Endpoint<UID>> getBindEndpoint();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * 创建消息
     *
     * @param messageId 消息 Id
     * @param context   消息上下文
     * @return 返回消息 Id
     */
    Message<UID> createMessage(long messageId, MessageContext<UID> context);

}
