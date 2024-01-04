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
