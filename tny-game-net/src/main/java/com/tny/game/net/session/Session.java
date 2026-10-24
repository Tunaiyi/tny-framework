/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.session;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * 会话, 代表通选两端
 * <p>
 */
public interface Session extends Communicator, Connection, MessageSender, Executor {

    /**
     * @return 事件监听
     */
    SessionEventWatches events();

    /*
     * @return 会话ID
     */
    long getId();

    /**
     * 发送过滤器
     *
     * @return 返回发送过滤器
     */
    MessageHandleFilter getSendFilter();

    /**
     * 接受过滤器
     *
     * @return 返回发送过滤器
     */
    MessageHandleFilter getReceiveFilter();

    /**
     * 心跳
     */
    void heartbeat();

    /**
     * 设置发送过滤器
     *
     * @param filter 过滤器
     */
    void setSendFilter(MessageHandleFilter filter);

    /**
     * 设置发接受过滤器
     *
     * @param filter 过滤器
     */
    void setReceiveFilter(MessageHandleFilter filter);

    /**
     * 重发发送消息
     *
     * @param tunnel 发送的通道
     * @param filter 消息筛选器
     */
    void resend(NetTunnel tunnel, Predicate<Message> filter);

    /**
     * 重发从fromId开始的已发送消息
     *
     * @param tunnel 发送的通道
     * @param fromId 开始 id
     * @param bound  是否包含 toId
     */
    void resend(NetTunnel tunnel, long fromId, FilterBound bound);

    /**
     * 重发从fromId到toId的已发送消息
     *
     * @param tunnel 发送的通道
     * @param fromId 开始 id
     * @param toId   结束 id
     * @param bound  是否包含 fromId & toId
     */
    void resend(NetTunnel tunnel, long fromId, long toId, FilterBound bound);

    /**
     * @return 获取所有发送的消息
     */
    List<Message> getAllSendMessages();

    /**
     * @return 获取 session 状态
     */
    SessionStatus getStatus();

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
