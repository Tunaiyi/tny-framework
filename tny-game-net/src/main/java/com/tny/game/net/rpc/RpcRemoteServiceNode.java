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
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcRemoteServiceNode implements RpcRemoteNode, RpcForwardNode {

    private final int serverId;

    private final Map<Long, RpcRemoteServiceAccess> remoteServiceAccessMap = new HashMap<>();

    private volatile List<RpcServiceAccess> orderAccessPoints = ImmutableList.of();

    private final RpcRemoteServiceSet service;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public RpcRemoteServiceNode(int serverId, RpcRemoteServiceSet service) {
        this.serverId = serverId;
        this.service = service;
    }

    @Override
    public int getNodeId() {
        return serverId;
    }

    @Override
    public RpcServiceType getServiceType() {
        return service.getServiceType();
    }

    @Override
    public List<? extends RpcRemoteAccess> getOrderRemoteAccesses() {
        return orderAccessPoints;
    }

    @Override
    public List<? extends RpcForwardAccess> getOrderForwardAccess() {
        return orderAccessPoints;
    }

    public RpcRemoteAccess anyGet() {
        List<? extends RpcRemoteAccess> orderAccessPoints = this.orderAccessPoints;
        if (orderAccessPoints.isEmpty()) {
            return null;
        }
        return orderAccessPoints.get(ThreadLocalRandom.current().nextInt(orderAccessPoints.size()));
    }

    private void readLock() {
        readWriteLock.readLock().lock();
    }

    private void readUnlock() {
        readWriteLock.readLock().unlock();
    }

    private void writeLock() {
        readWriteLock.writeLock().lock();
    }

    private void writeUnlock() {
        readWriteLock.writeLock().unlock();
    }

    @Override
    public RpcServiceAccess getForwardAccess(long id) {
        readLock();
        try {
            return remoteServiceAccessMap.get(id);
        } finally {
            readUnlock();
        }
    }

    @Override
    public RpcRemoteAccess getRemoteAccess(long id) {
        readLock();
        try {
            return remoteServiceAccessMap.get(id);
        } finally {
            readUnlock();
        }
    }

    @Override
    public boolean isActive() {
        return !orderAccessPoints.isEmpty();
    }

    protected void addEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        writeLock();
        try {
            boolean activate = this.remoteServiceAccessMap.isEmpty();
            RpcAccessIdentify nodeId = endpoint.getUserId();
            this.remoteServiceAccessMap.put(nodeId.getId(), new RpcRemoteServiceAccess(endpoint));
            this.orderAccessPoints = ImmutableList.sortedCopyOf(
                    Comparator.comparing(RpcRemoteAccess::getAccessId),
                    remoteServiceAccessMap.values());
            if (!activate && !this.remoteServiceAccessMap.isEmpty()) {
                service.onNodeActivate(this);
            }
        } finally {
            writeUnlock();
        }
    }

    protected void removeEndpoint(Endpoint<RpcAccessIdentify> endpoint) {
        writeLock();
        try {
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
                        Comparator.comparing(RpcRemoteAccess::getAccessId),
                        remoteServiceAccessMap.values());
                if (activate && this.remoteServiceAccessMap.isEmpty()) {
                    service.onNodeUnactivated(this);
                }
            }
        } finally {
            writeUnlock();
        }
    }

}