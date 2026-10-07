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

import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.session.listener.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 会话持有器
 */
public interface SessionKeeper {

    /**
     * @return 获取用户类型
     */
    ContactType getContactType();

    /**
     * @return 用户组类型
     */
    default String getContactGroup() {
        return this.getContactType().getGroup();
    }

    /**
     * <p>
     * <p>
     * 获取指定identify对应的Session <br>
     *
     * @param identify 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    Session getSession(long identify);

    /**
     * 获取所有的sessions
     *
     * @return 返回sessions map
     */
    Map<Long, Session> getAllSessions();

    /**
     * 发信息给用户 <br>
     *
     * @param identify 用户ID
     * @param context  消息内容
     */
    void sendTo(long identify, MessageContent context);

    /**
     * 发信息给用户集合 <br>
     *
     * @param identifiers 用户ID列表
     * @param context     消息内容
     */
    void sendTo(Collection<Long> identifiers, MessageContent context);

    /**
     * 发信息给用户集合 <br>
     *
     * @param identifysStream 用户ID流
     * @param context         消息内容
     */
    void sendTo(Stream<Long> identifysStream, MessageContent context);

    /**
     * 发送给所有在线的用户 <br>
     */
    /**
     * 全体广播：投递大管家当前注册的全部会话（含离线保留会话——离线消息进入其重发窗口供恢复补收，
     * 有意的必达设计，契约见 net-session 规格"全体广播覆盖全部注册会话且离线消息入窗"）。
     */
    void send2All(MessageContent context);

    /**
     * @deprecated 名称误标"仅在线"，实际投递全部注册会话；请改用 {@link #send2All(MessageContent)}（桥接保留，行为一致）。
     */
    @Deprecated
    default void send2AllOnline(MessageContent context) {
        send2All(context);
    }

    /**
     * 使指定identify的session关闭
     *
     * @param identify 指定identify
     * @return 返回下线session
     */
    Session close(long identify);

    /**
     * 使指定identify的session下线
     *
     * @param identify 指定identify
     * @return 返回下线session
     */
    Session offline(long identify);

    /**
     * 使所有session下线
     */
    void offlineAll();

    /**
     * 是所有session关闭
     */
    void closeAll();

    /**
     * @return 所有session数量
     */
    int size();

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    void addListener(SessionKeeperListener listener);

    /**
     * 添加监听器列表
     *
     * @param listeners 监听器列表
     */
    void addListener(Collection<SessionKeeperListener> listeners);

    /**
     * 移除监听器
     *
     * @param listener 监听器
     */
    void removeListener(SessionKeeperListener listener);

    /**
     * 计算在线人数
     */
    int countOnlineSize();

    /**
     * <p>
     * 添加指定的session<br>
     *
     * @param tunnel 注册tunnel
     * @throws AuthFailedException 认证异常
     */
    Optional<Session> online(Certificate certificate, NetTunnel tunnel) throws AuthFailedException;

    /**
     * <p>
     * <p>
     * 获取指定identify对应的Session <br>
     *
     * @param identify 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    boolean isOnline(long identify);

}
