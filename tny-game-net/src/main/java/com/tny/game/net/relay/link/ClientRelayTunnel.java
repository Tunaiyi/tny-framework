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
package com.tny.game.net.relay.link;

import java.util.Set;

/**
 * 本地可转发的通讯管道
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:47 下午
 */
public interface ClientRelayTunnel extends NetRelayTunnel {

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
     * 根据 集群id 获取转发连接
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