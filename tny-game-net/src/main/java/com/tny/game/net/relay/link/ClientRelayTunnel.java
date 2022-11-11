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
package com.tny.game.net.relay.link;

import java.util.Set;

/**
 * 本地可转发的通讯管道
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:47 下午
 */
public interface ClientRelayTunnel<UID> extends NetRelayTunnel<UID> {

    /**
     * 绑定转发连接
     *
     * @param link 转发连接
     */
    void bindLink(ClientRelayLink link);

    /**
     * 解绑转发连接
     *
     * @param link 转发连接
     */
    void unbindLink(ClientRelayLink link);

    /**
     * 更具 集群id 获取转发连接
     *
     * @param service 服务名
     * @return 返回获取的转发连接
     */
    ClientRelayLink getLink(String service);

    /**
     * @return 获取所有转发连接的 key
     */
    Set<String> getLinkKeys();

}