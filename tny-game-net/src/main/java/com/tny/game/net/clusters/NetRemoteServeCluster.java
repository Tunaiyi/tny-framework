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

package com.tny.game.net.clusters;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;

/**
 * 本地集群客户端管理
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public interface NetRemoteServeCluster extends RemoteServeCluster {

    /**
     * 关闭本地集群连接
     */
    void close();

    /**
     * 注册 LocaleServeInstance, 如果存在返回旧的 instance
     *
     * @param instance 注册的 instance
     * @return 返回 instance
     */
    RelayServeInstance registerInstance(NetRelayServeInstance instance);

    /**
     * 刷新实例
     */
    void refreshInstances();

    /**
     * 卸载指定 instanceId 的 Instance
     *
     * @param instanceId 指定的Instance id
     */
    void unregisterInstance(long instanceId);

    /**
     * @param node 更新节点信息
     */
    void updateInstance(ServeNode node);

}
