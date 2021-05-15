package com.tny.game.net.endpoint;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.List;
import java.util.function.Predicate;

/**
 * 终端, 代表通选两端
 * <p>
 */
public interface Endpoint<UID> extends Netter<UID>, Sender<UID> {

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
     */
    void resend(NetTunnel<UID> tunnel, Predicate<Message> filter);

    /**
     * 重发从fromId开始的已发送消息
     *
     * @param tunnel 发送的通道
     * @param fromId 开始 id
     * @param bound  是否包含 toId
     */
    void resend(NetTunnel<UID> tunnel, long fromId, FilterBound bound);

    /**
     * 重发从fromId到toId的已发送消息
     *
     * @param tunnel 发送的通道
     * @param fromId 开始 id
     * @param toId   结束 id
     * @param bound  是否包含 fromId & toId
     */
    void resend(NetTunnel<UID> tunnel, long fromId, long toId, FilterBound bound);

    /**
     * @return 获取所有发送的消息
     */
    List<Message> getAllSendMessages();

    /**
     * @return 获取 session 状态
     */
    EndpointStatus getStatus();

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
