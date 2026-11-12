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
package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.application.*;
import com.tny.game.net.session.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcServiceNodeSet implements RpcInvokeNodeSet, RpcForwardNodeSet {

    private final RpcServiceType serviceType;

    private final Map<Integer, RpcServiceNode> remoteNodeMap = new ConcurrentHashMap<>();

    private volatile List<RpcServiceNode> orderRemoteNodes = ImmutableList.of();

    // 快照发布锁：重建-赋值原子化，消除并发变更下"旧快照晚发布覆盖新快照"（net-rpc-registry 发布原子性）
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceNodeSet.class);

    private final ReentrantLock publishLock = new java.util.concurrent.locks.ReentrantLock();

    public RpcServiceNodeSet(RpcServiceType serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public RpcServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public List<? extends RpcForwardNode> getOrderForwarderNodes() {
        return orderRemoteNodes;
    }

    @Override
    public List<? extends RpcInvokeNode> getOrderInvokeNodes() {
        return orderRemoteNodes;
    }

    @Override
    public RpcInvokeNode findInvokeNode(int nodeId) {
        return remoteNodeMap.get(nodeId);
    }

    @Override
    public RpcAccess findInvokeAccess(int nodeId, long accessId) {
        RpcInvokeNode node = remoteNodeMap.get(nodeId);
        if (node == null) {
            return null;
        }
        return node.getAccess(accessId);
    }

    @Override
    public RpcAccess findForwardAccess(RpcAccessPoint accessPoint) {
        RpcServiceNode remoteNode = remoteNodeMap.get(accessPoint.getServerId());
        if (remoteNode == null) {
            return null;
        }
        RpcServiceAccess access = remoteNode.getForwardAccess(accessPoint.getContactId());
        if (access != null) {
            return access;
        }
        RpcAccess fallback = remoteNode.anyGet();
        if (fallback != null) {
            // 降级回退必须留痕（net-rpc-registry"路由回退有边界且留痕"）
            LOGGER.warn("指定接入 {} 未命中，降级选中同节点[{}]的其他接入 accessId {}", accessPoint.getContactId(),
                    accessPoint.getServerId(), fallback.getAccessId());
        }
        return fallback;
    }

    protected void addSession(Session session) {
        RpcServiceNode node = loadOrCreate(session);
        node.addSession(session);
        publishSnapshot();
    }

    protected void removeSession(Session session) {
        var opt = session.identifyToken(RpcAccessIdentify.class);
        if (opt.isEmpty()) {
            LOGGER.warn("移除接入点事件缺少 RpcAccessIdentify token，忽略：{}", session);
            return;
        }
        int serverId = opt.get().getServerId();
        RpcServiceNode node = remoteNodeMap.get(serverId);
        if (node == null) {
            // 陌生节点的移除事件：忽略留痕，不得为不存在节点建空壳（僵尸复活防线）
            return;
        }
        node.removeSession(session);
        if (node.isEmpty()) {
            remoteNodeMap.remove(serverId, node);
        }
        publishSnapshot();
    }

    protected void onNodeActivate(RpcServiceNode rpcNode) {
        publishSnapshot();
    }

    protected void onNodeUnactivated(RpcServiceNode rpcNode) {
        publishSnapshot();
    }

    private RpcServiceNode loadOrCreate(Session session) {
        var opt = session.identifyToken(RpcAccessIdentify.class);
        if (opt.isEmpty()) {
            throw new NullPointerException("RpcAccessIdentify token is null");
        }
        RpcAccessIdentify nodeId = opt.get();
        return remoteNodeMap.computeIfAbsent(nodeId.getServerId(), (serverId) -> new RpcServiceNode(serverId, this));
    }

    private void publishSnapshot() {
        this.publishLock.lock();
        try {
            this.orderRemoteNodes = ImmutableList.sortedCopyOf(Comparator.comparing(RpcInvokeNode::getNodeId),
                    this.remoteNodeMap.values().stream().filter(RpcInvokeNode::isActive).collect(Collectors.toList()));
        } finally {
            this.publishLock.unlock();
        }
    }


}