/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.cluster;

import com.tny.game.net.relay.cluster.watch.*;

import java.util.List;

/**
 * ServeNode 客户端, 用于获取与订阅ServeNode信息
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 2:52 下午
 */
public interface ServeNodeClient {

    /**
     * 获取指定 serveName 的所有ServeNode
     *
     * @param serveName 指定 serveName
     * @return 返回所有ServeNode
     */
    List<ServeNode> getAllServeNodes(String serveName);

    /**
     * 获取指定 serveName 的所有健康的ServeNode
     *
     * @param serveName 指定 serveName
     * @return 返回所有健康ServeNode
     */
    List<ServeNode> getHealthyServeNodes(String serveName);

    /**
     * 获取指定 serveName 和 id 的ServeNode
     *
     * @param serveName 指定服务名
     * @param id        节点 id
     * @return 返回服务节点
     */
    ServeNode getServeNode(String serveName, long id);

    /**
     * 获取指定 serveName 和 id 健康的ServeNode
     *
     * @param serveName 指定服务名
     * @param id        节点 id
     * @return 返回健康服务节点, 不存在或不健康返回 null
     */
    ServeNode getHealthyServeNode(String serveName, long id);

    /**
     * 订阅指定服务
     *
     * @param serveName 服务名
     * @param listener  监听器
     */
    void subscribe(String serveName, ServeNodeListener listener);

    /**
     * 退订指定服务
     *
     * @param serveName 服务名
     * @param listener  监听器
     */
    void unsubscribe(String serveName, ServeNodeListener listener);

}
