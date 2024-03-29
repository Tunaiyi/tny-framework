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
import com.tny.game.net.session.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 3:31 下午
 */
public class RpcServiceNode implements RpcInvokeNode, RpcForwardNode {

    private final int serverId;

    private final Map<Long, RpcRemoteServiceAccess> remoteServiceAccessMap = new HashMap<>();

    private volatile List<RpcServiceAccess> orderAccessPoints = ImmutableList.of();

    private final RpcServiceNodeSet service;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public RpcServiceNode(int serverId, RpcServiceNodeSet service) {
        this.serverId = serverId;
        this.service = service;
    }

    @Override
    public int getNodeId() {
        return serverId;
    }

    @Override
    public ContactType getServiceType() {
        return service.getServiceType();
    }

    @Override
    public List<? extends RpcAccess> getOrderAccesses() {
        return orderAccessPoints;
    }

    @Override
    public List<? extends RpcForwardAccess> getOrderForwardAccess() {
        return orderAccessPoints;
    }

    public RpcAccess anyGet() {
        List<? extends RpcAccess> orderAccessPoints = this.orderAccessPoints;
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
    public RpcAccess getAccess(long id) {
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

    protected void addSession(Session session) {
        writeLock();
        try {
            boolean activate = this.remoteServiceAccessMap.isEmpty();
            RpcAccessIdentify nodeId = session.getIdentifyToken(RpcAccessIdentify.class);
            this.remoteServiceAccessMap.put(nodeId.getContactId(), new RpcRemoteServiceAccess(session));
            this.orderAccessPoints = ImmutableList.sortedCopyOf(Comparator.comparing(RpcAccess::getAccessId), remoteServiceAccessMap.values());
            if (!activate && !this.remoteServiceAccessMap.isEmpty()) {
                service.onNodeActivate(this);
            }
        } finally {
            writeUnlock();
        }
    }

    protected void removeSession(Session session) {
        writeLock();
        try {
            RpcAccessIdentify nodeId = session.getIdentifyToken(RpcAccessIdentify.class);
            boolean activate = this.remoteServiceAccessMap.isEmpty();
            RpcRemoteServiceAccess accessPoint = this.remoteServiceAccessMap.get(nodeId.getContactId());
            if (accessPoint == null) {
                return;
            }
            if (accessPoint.getSession() != session) {
                return;
            }
            if (this.remoteServiceAccessMap.remove(nodeId.getContactId(), accessPoint)) {
                this.orderAccessPoints = ImmutableList.sortedCopyOf(Comparator.comparing(RpcAccess::getAccessId), remoteServiceAccessMap.values());
                if (activate && this.remoteServiceAccessMap.isEmpty()) {
                    service.onNodeUnactivated(this);
                }
            }
        } finally {
            writeUnlock();
        }
    }

}