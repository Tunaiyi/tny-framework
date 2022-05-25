package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcRemoteServiceSet implements RpcRemoterSet, RpcForwardSet {

    private final RpcServiceType serviceType;

    private final Map<Integer, RpcRemoteServiceNode> remoteNodeMap = new ConcurrentHashMap<>();

    private volatile List<RpcRemoteServiceNode> orderRemoteNodes = ImmutableList.of();

    private final AtomicInteger version = new AtomicInteger(0);

    public RpcRemoteServiceSet(RpcServiceType serviceType) {
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
    public List<? extends RpcRemoterNode> getOrderRemoterNodes() {
        return orderRemoteNodes;
    }

    @Override
    public RpcRemoterNode findRemoterNode(int nodeId) {
        return remoteNodeMap.get(nodeId);
    }

    @Override
    public RpcRemoterAccess findRemoterAccess(int nodeId, long accessId) {
        RpcRemoterNode node = remoteNodeMap.get(nodeId);
        if (node == null) {
            return null;
        }
        return node.getRemoterAccess(accessId);
    }

    public int getVersion() {
        return version.get();
    }

    private void updateVersion() {
        version.incrementAndGet();
    }

    @Override
    public RpcRemoterAccess findForwardAccess(RpcServicer servicer) {
        RpcRemoteServiceNode remoteNode = remoteNodeMap.get(servicer.getServerId());
        if (remoteNode == null) {
            return null;
        }
        RpcServiceAccess access = remoteNode.getForwardAccess(servicer.getId());
        if (access != null) {
            return access;
        }
        return remoteNode.anyGet();
    }

    protected void addEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        RpcRemoteServiceNode node = loadOrCreate(endpoint);
        node.addEndpoint(endpoint);
        refreshNodes(node);
    }

    protected void removeEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        RpcRemoteServiceNode node = loadOrCreate(endpoint);
        node.removeEndpoint(endpoint);
        refreshNodes(node);
    }

    protected void onNodeActivate(RpcRemoteServiceNode rpcNode) {
        refreshNodes(rpcNode);
    }

    protected void onNodeUnactivated(RpcRemoteServiceNode rpcNode) {
        refreshNodes(rpcNode);
    }

    private RpcRemoteServiceNode loadOrCreate(Endpoint<RpcAccessIdentify> endpoint) {
        RpcAccessIdentify nodeId = endpoint.getUserId();
        return remoteNodeMap.computeIfAbsent(nodeId.getServerId(), (serverId) -> new RpcRemoteServiceNode(serverId, this));
    }

    private void refreshNodes(RpcRemoteServiceNode rpcNode) {
        RpcRemoteServiceNode currentNode = remoteNodeMap.get(rpcNode.getNodeId());
        if (currentNode == rpcNode) {
            orderRemoteNodes = ImmutableList.sortedCopyOf(Comparator.comparing(RpcRemoterNode::getNodeId),
                    remoteNodeMap.values().stream().filter(RpcRemoterNode::isActive).collect(Collectors.toList()));
            this.updateVersion();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(System.nanoTime() * 100 + ThreadLocalRandom.current().nextInt(100));
        }
    }

}