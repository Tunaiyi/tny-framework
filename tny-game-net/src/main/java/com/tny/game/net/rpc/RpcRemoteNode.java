package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcRemoteNode {

    private final int serverId;

    private final Map<Long, RpcEndpointAccessPoint> realEndpointMap = new HashMap<>();

    private volatile List<RpcRemoteAccessPoint> orderAccessPoints = ImmutableList.of();

    ;

    private final RpcRemoteServiceSet service;

    public RpcRemoteNode(int serverId, RpcRemoteServiceSet service) {
        this.serverId = serverId;
        this.service = service;
    }

    public int getServerId() {
        return serverId;
    }

    public List<RpcRemoteAccessPoint> getOrderAccessPoints() {
        return orderAccessPoints;
    }

    public RpcRemoteAccessPoint anyGet() {
        List<RpcRemoteAccessPoint> orderAccessPoints = this.orderAccessPoints;
        if (orderAccessPoints.isEmpty()) {
            return null;
        }
        return orderAccessPoints.get(ThreadLocalRandom.current().nextInt(orderAccessPoints.size()));
    }

    public RpcRemoteAccessPoint get(long id) {
        return realEndpointMap.get(id);
    }

    public boolean isActive() {
        return !orderAccessPoints.isEmpty();
    }

    protected void addEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        synchronized (this) {
            boolean activate = this.realEndpointMap.isEmpty();
            RpcAccessIdentify nodeId = endpoint.getUserId();
            this.realEndpointMap.put(nodeId.getId(), new RpcEndpointAccessPoint(endpoint));
            this.orderAccessPoints = ImmutableList.sortedCopyOf(
                    Comparator.comparing(RpcRemoteAccessPoint::getAccessId),
                    realEndpointMap.values());
            if (!activate && !this.realEndpointMap.isEmpty()) {
                service.onNodeActivate(this);
            }
        }
    }

    protected void removeEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        synchronized (this) {
            RpcAccessIdentify nodeId = endpoint.getUserId();
            boolean activate = this.realEndpointMap.isEmpty();
            RpcEndpointAccessPoint accessPoint = this.realEndpointMap.get(nodeId.getId());
            if (accessPoint == null) {
                return;
            }
            if (accessPoint.getEndpoint() != endpoint) {
                return;
            }
            if (this.realEndpointMap.remove(nodeId.getId(), accessPoint)) {
                this.orderAccessPoints = ImmutableList.sortedCopyOf(
                        Comparator.comparing(RpcRemoteAccessPoint::getAccessId),
                        realEndpointMap.values());
                if (activate && this.realEndpointMap.isEmpty()) {
                    service.onNodeUnactivated(this);
                }
            }
        }
    }

}