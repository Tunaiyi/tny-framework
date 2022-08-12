/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

import java.util.List;

/**
 * 本地(客户端连接在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
@UnitInterface
public interface RemoteRelayExplorer extends RelayExplorer<RemoteRelayTunnel<?>> {

    /**
     * 获取指定 id 的集群
     *
     * @param id id
     * @return 返回获取集群
     */
    RemoteServeCluster getCluster(String id);

    /**
     * @return 获取所有集群列表
     */
    List<RemoteServeCluster> getClusters();

    /**
     * 创建可转发的本地管道, 并且关联转发的目标服务
     *
     * @param id        管道id
     * @param transport 通讯器
     * @param context   网络上下文
     * @return 返回创建的管道
     */
    <D> DoneResult<RemoteRelayTunnel<D>> createTunnel(long id, MessageTransporter transport, NetworkContext context);

    /**
     * 为通讯管道分配指定的集群转发连接
     *
     * @param tunnel  分配的通讯管道
     * @param cluster 集群 id
     * @return 返回分配的连接
     */
    <D> RemoteRelayLink allotLink(RemoteRelayTunnel<D> tunnel, String cluster);

}
