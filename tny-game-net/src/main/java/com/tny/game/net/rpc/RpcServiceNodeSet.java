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
package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.application.*;
import com.tny.game.net.endpoint.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final AtomicInteger version = new AtomicInteger(0);

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

    public int getVersion() {
        return version.get();
    }

    private void updateVersion() {
        version.incrementAndGet();
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
        return remoteNode.anyGet();
    }

    protected void addEndpoint(Endpoint endpoint) {
        RpcServiceNode node = loadOrCreate(endpoint);
        node.addEndpoint(endpoint);
        refreshNodes(node);
    }

    protected void removeEndpoint(Endpoint endpoint) {
        RpcServiceNode node = loadOrCreate(endpoint);
        node.removeEndpoint(endpoint);
        refreshNodes(node);
    }

    protected void onNodeActivate(RpcServiceNode rpcNode) {
        refreshNodes(rpcNode);
    }

    protected void onNodeUnactivated(RpcServiceNode rpcNode) {
        refreshNodes(rpcNode);
    }

    private RpcServiceNode loadOrCreate(Endpoint endpoint) {
        var opt = endpoint.identifyToken(RpcAccessIdentify.class);
        if (opt.isEmpty()) {
            throw new NullPointerException("RpcAccessIdentify token is null");
        }
        RpcAccessIdentify nodeId = opt.get();
        return remoteNodeMap.computeIfAbsent(nodeId.getServerId(), (serverId) -> new RpcServiceNode(serverId, this));
    }

    private void refreshNodes(RpcServiceNode rpcNode) {
        RpcServiceNode currentNode = remoteNodeMap.get(rpcNode.getNodeId());
        if (currentNode == rpcNode) {
            orderRemoteNodes = ImmutableList.sortedCopyOf(Comparator.comparing(RpcInvokeNode::getNodeId),
                    remoteNodeMap.values().stream().filter(RpcInvokeNode::isActive).collect(Collectors.toList()));
            this.updateVersion();
        }
    }


}