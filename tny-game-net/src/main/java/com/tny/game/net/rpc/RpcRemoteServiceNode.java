package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcRemoteServiceNode implements RpcRemoterNode, RpcForwardNode {

    private final int serverId;

    private final Map<Long, RpcRemoteServiceAccess> remoteServiceAccessMap = new HashMap<>();

    private volatile List<RpcServiceAccess> orderAccessPoints = ImmutableList.of();

    private final RpcRemoteServiceSet service;

    public RpcRemoteServiceNode(int serverId, RpcRemoteServiceSet service) {
        this.serverId = serverId;
        this.service = service;
    }

    @Override
    public int getNodeId() {
        return serverId;
    }

    @Override
    public List<? extends RpcRemoterAccess> getOrderRemoterAccesses() {
        return orderAccessPoints;
    }

    @Override
    public List<? extends RpcForwardAccess> getOrderForwardAccess() {
        return orderAccessPoints;
    }

    public RpcRemoterAccess anyGet() {
        List<? extends RpcRemoterAccess> orderAccessPoints = this.orderAccessPoints;
        if (orderAccessPoints.isEmpty()) {
            return null;
        }
        return orderAccessPoints.get(ThreadLocalRandom.current().nextInt(orderAccessPoints.size()));
    }

    @Override
    public RpcServiceAccess getForwardAccess(long id) {
        return remoteServiceAccessMap.get(id);
    }

    @Override
    public RpcRemoterAccess getRemoterAccess(long id) {
        return remoteServiceAccessMap.get(id);
    }

    @Override
    public boolean isActive() {
        return !orderAccessPoints.isEmpty();
    }

    protected void addEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        synchronized (this) {
            boolean activate = this.remoteServiceAccessMap.isEmpty();
            RpcAccessIdentify nodeId = endpoint.getUserId();
            this.remoteServiceAccessMap.put(nodeId.getId(), new RpcRemoteServiceAccess(endpoint));
            this.orderAccessPoints = ImmutableList.sortedCopyOf(
                    Comparator.comparing(RpcRemoterAccess::getAccessId),
                    remoteServiceAccessMap.values());
            if (!activate && !this.remoteServiceAccessMap.isEmpty()) {
                service.onNodeActivate(this);
            }
        }
    }

    protected void removeEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        synchronized (this) {
            RpcAccessIdentify nodeId = endpoint.getUserId();
            boolean activate = this.remoteServiceAccessMap.isEmpty();
            RpcRemoteServiceAccess accessPoint = this.remoteServiceAccessMap.get(nodeId.getId());
            if (accessPoint == null) {
                return;
            }
            if (accessPoint.getEndpoint() != endpoint) {
                return;
            }
            if (this.remoteServiceAccessMap.remove(nodeId.getId(), accessPoint)) {
                this.orderAccessPoints = ImmutableList.sortedCopyOf(
                        Comparator.comparing(RpcRemoterAccess::getAccessId),
                        remoteServiceAccessMap.values());
                if (activate && this.remoteServiceAccessMap.isEmpty()) {
                    service.onNodeUnactivated(this);
                }
            }
        }
    }

}