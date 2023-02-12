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
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
@GlobalEventListener
public class BaseRpcServicerManager implements RpcServicerManager, EndpointKeeperCreateListener<Object> {

    private final Map<MessagerType, RpcServiceNodeSet> serviceSetMap = new CopyOnWriteMap<>();

    private final Map<MessagerType, RpcInvokeNodeSet> invokeNodeSetMap = new ConcurrentHashMap<>();

    private static final HashLock<Lock> lockPool = HashLock.common();

    @Override
    public RpcForwardNodeSet loadForwardNodeSet(RpcServiceType type) {
        return (RpcForwardNodeSet)loadInvokeNodeSet(type);
    }

    @Override
    public RpcForwardNodeSet findForwardNodeSet(RpcServiceType serviceType) {
        return serviceSetMap.get(serviceType);
    }

    @Override
    public RpcInvokeNodeSet loadInvokeNodeSet(MessagerType serviceType) {
        return doLoadInvokeNodeSet(serviceType, null);
    }

    private RpcInvokeNodeSet doLoadInvokeNodeSet(MessagerType messagerType, Consumer<MessagerNodeSet> consumer) {
        if (messagerType instanceof RpcServiceType) {
            return doLoadRpcServiceSet((RpcServiceType)messagerType);
        } else {
            return doLoadRpcServiceSet(messagerType, consumer);
        }
    }

    @Override
    public RpcInvokeNodeSet findInvokeNodeSet(MessagerType serviceType) {
        return serviceSetMap.get(serviceType);
    }

    private RpcInvokeNodeSet doLoadRpcServiceSet(MessagerType messagerType, Consumer<MessagerNodeSet> consumer) {
        var nodeSet = invokeNodeSetMap.get(messagerType);
        if (nodeSet != null) {
            return nodeSet;
        }
        MessagerNodeSet newSet = new MessagerNodeSet(messagerType);
        if (invokeNodeSetMap.putIfAbsent(messagerType, newSet) == null) {
            if (consumer != null) {
                consumer.accept(newSet);
            }
        }
        return invokeNodeSetMap.get(messagerType);
    }

    private RpcServiceNodeSet doLoadRpcServiceSet(RpcServiceType type) {
        RpcServiceNodeSet exist = serviceSetMap.get(type);
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
            RpcServiceNodeSet serviceSet = new RpcServiceNodeSet(type);
            this.serviceSetMap.put(type, serviceSet);
            this.invokeNodeSetMap.put(type, serviceSet);
            return serviceSet;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onCreate(EndpointKeeper<Object, Endpoint<Object>> keeper) {
        MessagerType messagerType = keeper.getMessagerType();
        if (messagerType instanceof RpcServiceType) {
            RpcServiceType serviceType = as(messagerType, RpcServiceType.class);
            RpcServiceNodeSet servicer = doLoadRpcServiceSet(serviceType);
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
            var nodeSet = doLoadInvokeNodeSet(messagerType, null);
            if (nodeSet instanceof MessagerNodeSet) {
                var messagerSet = (MessagerNodeSet)nodeSet;
                messagerSet.bind(keeper);
            }
        }

    }

}