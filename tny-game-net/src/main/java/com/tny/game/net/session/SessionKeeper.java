/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
    void send2AllOnline(MessageContent context);

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
