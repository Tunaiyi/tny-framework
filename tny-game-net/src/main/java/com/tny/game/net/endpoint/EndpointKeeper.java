/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 终端持有器
 *
 * @param <UID>
 * @param <E>
 */
public interface EndpointKeeper<UID, E extends Endpoint<UID>> {

    /**
     * @return 获取用户类型
     */
    MessagerType getMessagerType();

    /**
     * @return 用户组类型
     */
    default String getUserGroup() {
        return this.getMessagerType().getGroup();
    }

    /**
     * <p>
     * <p>
     * 获取指定userId对应的Session <br>
     *
     * @param userId 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    E getEndpoint(UID userId);

    /**
     * 获取所有的sessions
     *
     * @return 返回sessions map
     */
    Map<UID, E> getAllEndpoints();

    /**
     * 发信息给用户 <br>
     *
     * @param userId  用户ID
     * @param context 消息内容
     */
    void send2User(UID userId, MessageContext context);

    /**
     * 发信息给用户集合 <br>
     *
     * @param userIds 用户ID列表
     * @param context 消息内容
     */
    void send2Users(Collection<UID> userIds, MessageContext context);

    /**
     * 发信息给用户集合 <br>
     *
     * @param userIdsStream 用户ID流
     * @param context       消息内容
     */
    void send2Users(Stream<UID> userIdsStream, MessageContext context);

    /**
     * 发送给所有在线的用户 <br>
     */
    void send2AllOnline(MessageContext context);

    /**
     * 使指定userId的session关闭
     *
     * @param userId 指定userId
     * @return 返回下线session
     */
    E close(UID userId);

    /**
     * 使指定userId的endpoint下线
     *
     * @param userId 指定userId
     * @return 返回下线endpoint
     */
    E offline(UID userId);

    /**
     * 使所有endpoint下线
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
    void addListener(EndpointKeeperListener<UID> listener);

    /**
     * 添加监听器列表
     *
     * @param listeners 监听器列表
     */
    void addListener(Collection<EndpointKeeperListener<UID>> listeners);

    /**
     * 移除监听器
     *
     * @param listener 监听器
     */
    void removeListener(EndpointKeeperListener<UID> listener);

    /**
     * 计算在线人数
     */
    int countOnlineSize();

    /**
     * <p>
     * 添加指定的endpoint<br>
     *
     * @param tunnel 注册tunnel
     * @throws ValidatorFailException 认证异常
     */
    Optional<E> online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * <p>
     * <p>
     * 获取指定userId对应的Session <br>
     *
     * @param userId 指定的Key
     * @return 返回获取的endpoint, 无endpoint返回null
     */
    boolean isOnline(UID userId);

}
