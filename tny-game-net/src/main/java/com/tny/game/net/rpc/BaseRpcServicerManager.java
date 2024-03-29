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
import com.tny.game.common.event.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.session.*;
import com.tny.game.net.session.listener.*;

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
public class BaseRpcServicerManager implements RpcServicerManager, SessionKeeperCreateListener {

    private final Map<ContactType, RpcServiceNodeSet> serviceSetMap = new CopyOnWriteMap<>();

    private final Map<ContactType, RpcInvokeNodeSet> invokeNodeSetMap = new ConcurrentHashMap<>();

    private static final MapLocker<RpcServiceType, Lock> lockPool = MapLocker.common();

    @Override
    public RpcForwardNodeSet loadForwardNodeSet(RpcServiceType type) {
        return (RpcForwardNodeSet) loadInvokeNodeSet(type);
    }

    @Override
    public RpcForwardNodeSet findForwardNodeSet(RpcServiceType serviceType) {
        return serviceSetMap.get(serviceType);
    }

    @Override
    public RpcInvokeNodeSet loadInvokeNodeSet(ContactType serviceType) {
        return doLoadInvokeNodeSet(serviceType, null);
    }

    private RpcInvokeNodeSet doLoadInvokeNodeSet(ContactType contactType, Consumer<ContactNodeSet> consumer) {
        if (contactType instanceof RpcServiceType) {
            return doLoadRpcServiceSet((RpcServiceType) contactType);
        } else {
            return doLoadRpcServiceSet(contactType, consumer);
        }
    }

    @Override
    public RpcInvokeNodeSet findInvokeNodeSet(ContactType serviceType) {
        return serviceSetMap.get(serviceType);
    }

    private RpcInvokeNodeSet doLoadRpcServiceSet(ContactType contactType, Consumer<ContactNodeSet> consumer) {
        var nodeSet = invokeNodeSetMap.get(contactType);
        if (nodeSet != null) {
            return nodeSet;
        }
        ContactNodeSet newSet = new ContactNodeSet(contactType);
        if (invokeNodeSetMap.putIfAbsent(contactType, newSet) == null) {
            if (consumer != null) {
                consumer.accept(newSet);
            }
        }
        return invokeNodeSetMap.get(contactType);
    }

    private RpcServiceNodeSet doLoadRpcServiceSet(RpcServiceType type) {
        RpcServiceNodeSet exist = serviceSetMap.get(type);
        if (exist != null) {
            return exist;
        }
        Lock typeLock = lockPool.getLock(type);
        typeLock.lock();
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
            typeLock.unlock();
        }
    }

    @Override
    public void onCreate(SessionKeeper keeper) {
        ContactType contactType = keeper.getContactType();
        if (contactType instanceof RpcServiceType) {
            RpcServiceType serviceType = as(contactType, RpcServiceType.class);
            RpcServiceNodeSet nodeSet = doLoadRpcServiceSet(serviceType);
            SessionKeeper rpcKeeper = as(keeper);
            rpcKeeper.addListener(new SessionKeeperListener() {

                @Override
                public void onAddSession(SessionKeeper keeper, Session session) {
                    nodeSet.addSession(session);
                }

                @Override
                public void onRemoveSession(SessionKeeper keeper, Session session) {
                    nodeSet.removeSession(session);
                }

            });
        } else {
            var nodeSet = doLoadInvokeNodeSet(contactType, null);
            if (nodeSet instanceof ContactNodeSet contactNodeSet) {
                contactNodeSet.bind(keeper);
            }
        }

    }

}