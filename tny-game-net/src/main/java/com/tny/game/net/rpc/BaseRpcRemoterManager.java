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

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.concurrent.lock.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.listener.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
@GlobalEventListener
public class BaseRpcRemoterManager implements RpcServicerManager, EndpointKeeperCreateListener<Object> {

    private final Map<MessagerType, RpcRemoteServiceSet> serviceSetMap = new CopyOnWriteMap<>();

    private final Map<MessagerType, RpcRemoteSet> remoterSetMap = new ConcurrentHashMap<>();

    private static final HashLock<Lock> lockPool = HashLock.common();

    @Override
    public RpcForwardSet loadForwardSet(RpcServiceType type) {
        return (RpcForwardSet)loadRemoterSet(type);
    }

    @Override
    public RpcForwardSet findForwardSet(RpcServiceType serviceType) {
        return serviceSetMap.get(serviceType);
    }

    @Override
    public RpcRemoteSet loadRemoterSet(MessagerType type) {
        if (type instanceof RpcServiceType) {
            return loadServiceSet((RpcServiceType)type);
        } else {
            return remoterSetMap.computeIfAbsent(type, MessagerRemoter::new);
        }
    }

    private RpcRemoteServiceSet loadServiceSet(RpcServiceType type) {
        RpcRemoteServiceSet exist = serviceSetMap.get(type);
        if (exist != null) {
            return exist;
        }
        Lock lock = lockPool.getLock(type.getId());
        lock.lock();
        try {
            exist = serviceSetMap.get(type);
            if (exist != null) {
                return exist;
            }
            RpcRemoteServiceSet serviceSet = new RpcRemoteServiceSet((RpcServiceType)type);
            this.serviceSetMap.put(type, serviceSet);
            this.remoterSetMap.put(type, serviceSet);
            return serviceSet;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public RpcRemoteSet findRemoterSet(MessagerType type) {
        return serviceSetMap.get(type);
    }

    @Override
    public void onCreate(EndpointKeeper<Object, Endpoint<Object>> keeper) {
        MessagerType messagerType = keeper.getMessagerType();
        if (messagerType instanceof RpcServiceType) {
            RpcServiceType serviceType = as(messagerType, RpcServiceType.class);
            RpcRemoteServiceSet servicer = loadServiceSet(serviceType);
            EndpointKeeper<RpcAccessIdentify, Endpoint<RpcAccessIdentify>> rpcKeeper = as(keeper);
            rpcKeeper.addListener(new EndpointKeeperListener<RpcAccessIdentify>() {

                @Override
                public void onAddEndpoint(EndpointKeeper<RpcAccessIdentify, Endpoint<RpcAccessIdentify>> keeper,
                        Endpoint<RpcAccessIdentify> endpoint) {
                    servicer.addEndpoint(endpoint);
                }

                @Override
                public void onRemoveEndpoint(EndpointKeeper<RpcAccessIdentify, Endpoint<RpcAccessIdentify>> keeper,
                        Endpoint<RpcAccessIdentify> endpoint) {
                    servicer.removeEndpoint(endpoint);
                }

            });
        } else {
            RpcRemoteSet remoterSet = loadRemoterSet(messagerType);
            if (remoterSet instanceof MessagerRemoter) {
                ((MessagerRemoter)remoterSet).bind(keeper);
            }
        }

    }

}