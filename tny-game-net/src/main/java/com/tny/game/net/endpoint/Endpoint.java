package com.tny.game.net.endpoint;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.List;
import java.util.function.Predicate;

/**
 * 终端, 代表通选两端
 * <p>
 */
public interface Endpoint<UID> extends Netter<UID>, Receiver<UID>, Sender<UID> {

    /*
     * @return 终端ID
     */
    long getId();

    /**
     * 发送过滤器
     *
     * @return 返回发送过滤器
     */
    MessageHandleFilter<UID> getSendFilter();

    /**
     * 接受过滤器
     *
     * @return 返回发送过滤器
     */
    MessageHandleFilter<UID> getReceiveFilter();

    /**
     * 心跳
     */
    void heartbeat();

    /**
     * 设置发送过滤器
     *
     * @param filter 过滤器
     */
    void setSendFilter(MessageHandleFilter<UID> filter);

    /**
     * 设置发接受过滤器
     *
     * @param filter 过滤器
     */
    void setReceiveFilter(MessageHandleFilter<UID> filter);

    /**
     * 重发发送消息
     *
     * @param tunnel 发送的通道
     * @param filter 消息筛选器
     * @return 返回发送上下文
     */
    void resend(NetTunnel<UID> tunnel, Predicate<Message<UID>> filter);

    /**
     * 处理收到消息
     *
     * @param tunnel  通道
     * @param message 消息
     */
    boolean receive(NetTunnel<UID> tunnel, Message<UID> message);

    /**
     * 异步发送消息
     *
     * @param tunnel         发送的通道
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendContext<UID> send(NetTunnel<UID> tunnel, MessageContext<UID> messageContext);

    /**
     * @return 获取筛选的发送的消息
     */
    List<Message<UID>> getSendMessages(Predicate<Message<UID>> filter);

    /**
     * @return 获取所有发送的消息
     */
    List<Message<UID>> getAllSendMessages();

    /**
     * @return 获取 session 状态
     */
    EndpointState getState();


    /**
     * @return 获取下线时间
     */
    long getOfflineTime();

    /**
     * 断开连接
     */
    void offline();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOnline();

    /**
     * 是否已下线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOffline();

}
