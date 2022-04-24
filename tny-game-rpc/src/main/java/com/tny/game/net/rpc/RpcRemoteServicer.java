package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
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
public class RpcRemoteServicer {

    private final String name;

    private final Map<Long, RpcRemoteNode> remoteNodeMap = new ConcurrentHashMap<>();

    private volatile List<RpcRemoteNode> orderRemoteNodes = ImmutableList.of();

    private AtomicInteger version = new AtomicInteger(0);

    public RpcRemoteServicer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<RpcRemoteNode> getOrderRemoteNodes() {
        return orderRemoteNodes;
    }

    public int getVersion() {
        return version.get();
    }

    private void updateVersion() {
        version.incrementAndGet();
    }

    protected void addEndpoint(Endpoint<RpcAccessId> endpoint) {
        RpcRemoteNode node = loadOrCreate(endpoint);
        node.addEndpoint(endpoint);
        refreshNodes(node);
    }

    protected void removeEndpoint(Endpoint<RpcAccessId> endpoint) {
        RpcRemoteNode node = loadOrCreate(endpoint);
        node.removeEndpoint(endpoint);
        refreshNodes(node);
    }

    protected void onNodeActivate(RpcRemoteNode rpcNode) {
        refreshNodes(rpcNode);
    }

    protected void onNodeUnactivated(RpcRemoteNode rpcNode) {
        refreshNodes(rpcNode);
    }

    private RpcRemoteNode loadOrCreate(Endpoint<RpcAccessId> endpoint) {
        RpcAccessId nodeId = endpoint.getUserId();
        return remoteNodeMap.computeIfAbsent(nodeId.getServerId(), (serverId) -> new RpcRemoteNode(serverId, this));
    }

    private void refreshNodes(RpcRemoteNode rpcNode) {
        RpcRemoteNode currentNode = remoteNodeMap.get(rpcNode.getServerId());
        if (currentNode == rpcNode) {
            orderRemoteNodes = ImmutableList.sortedCopyOf(Comparator.comparing(RpcRemoteNode::getServerId),
                    remoteNodeMap.values().stream().filter(RpcRemoteNode::isActive).collect(Collectors.toList()));
            this.updateVersion();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(System.nanoTime() * 100 + ThreadLocalRandom.current().nextInt(100));
        }
    }

}