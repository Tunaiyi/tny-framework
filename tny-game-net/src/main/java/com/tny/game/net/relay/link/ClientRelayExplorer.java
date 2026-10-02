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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.clusters.*;
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
public interface ClientRelayExplorer extends RelayExplorer {

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
     * 获取指定的 tunnel
     *
     * @param instanceId 创建 tunnel 的服务实例 id
     * @param tunnelId   管道 id
     */
    @Override
    ClientRelayTunnel getTunnel(long instanceId, long tunnelId);

    /**
     * 创建可转发的本地管道, 并且关联转发的目标服务
     *
     * @param id        管道id
     * @param transport 通讯器
     * @param context   网络上下文
     * @return 返回创建的管道
     */
    DoneResult<ClientRelayTunnel> createTunnel(long id, MessageTransport transport, NetworkContext context);

    /**
     * 为通讯管道分配指定的集群转发连接
     *
     * @param tunnel  分配的通讯管道
     * @param cluster 集群 id
     * @return 返回分配的连接
     */
    ClientRelayLink allotLink(ClientRelayTunnel tunnel, String cluster);

}
