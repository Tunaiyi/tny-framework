/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        // 读全量注册表：serviceSetMap 只含 RPC 服务型，Contact 型退化为恒 null（load/find 不对称修复）
        return invokeNodeSetMap.get(serviceType);
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