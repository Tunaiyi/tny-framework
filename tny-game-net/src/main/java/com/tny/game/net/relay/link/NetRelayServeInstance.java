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

import com.tny.game.net.clusters.*;

import java.util.Map;

/**
 * 本地集群服务实例
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:24 下午
 */
public interface NetRelayServeInstance extends RelayServeInstance {

    /**
     * 注册转发连接
     *
     * @param link 集群实例
     */
    void register(ClientRelayLink link);

    /**
     * @param link 断开连接
     */
    void disconnected(ClientRelayLink link);

    /**
     * 释放转发连接
     *
     * @param link 集群实例
     */
    void relieve(ClientRelayLink link);

    /**
     * @return 获取登录用户名
     */
    String getUsername();

    /**
     * @param defaultName 默认吗
     * @return 如果登录用户名为空, 返回 defaultName, 否则返回用户名
     */
    String username(String defaultName);

    /**
     * 关闭服务实例
     */
    void close();

    /**
     * 服务实例心跳
     */
    void heartbeat();

    /**
     * 变健康
     */
    boolean updateHealthy(boolean healthy);

    /**
     * 更新Metadata
     *
     * @param metadata Metadata
     */
    void updateMetadata(Map<String, Object> metadata);

}
