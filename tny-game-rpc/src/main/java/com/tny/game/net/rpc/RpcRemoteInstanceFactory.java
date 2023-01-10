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

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.rpc.annotation.*;
import javassist.util.proxy.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
public class RpcRemoteInstanceFactory {

    private RpcRemoteSetting setting;

    private RpcMonitor rpcMonitor;

    private RpcInvokeNodeManager rpcRemoterManager;

    private RpcRouteManager rpcRouteManager;

    public RpcRemoteInstanceFactory(RpcRemoteSetting setting, RpcInvokeNodeManager rpcRemoterManager, RpcRouteManager rpcRouteManager,
            RpcMonitor rpcMonitor) {
        this.setting = setting;
        this.rpcRemoterManager = rpcRemoterManager;
        this.rpcRouteManager = rpcRouteManager;
        this.rpcMonitor = rpcMonitor;
    }

    public <T> T create(Class<?> rpcClass) {
        ProxyFactory factory = new ProxyFactory();
        RpcRemoteInstance instance = createRpcInstance(rpcClass);
        factory.setInterfaces(new Class[]{rpcClass});
        factory.setFilter((method) -> instance.invoker(method) != null);
        Class<?> c = factory.createClass();
        MethodHandler handler = (self, method, proceed, args) -> {
            RpcRemoteInvoker invoker = instance.invoker(method);
            return invoker.invoke(args);
        };
        Object proxy;
        try {
            proxy = c.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new RpcException(ResultCode.FAILURE, e);
        }
        ((Proxy)proxy).setHandler(handler);
        return as(proxy);
    }

    private RpcRemoteInstance createRpcInstance(Class<?> rpcClass) {
        List<RpcRemoteMethod> methods = RpcRemoteMethod.methodsOf(rpcClass);
        RpcRemoteService rpcService = rpcClass.getAnnotation(RpcRemoteService.class);
        String toService = rpcService.value();
        if (StringUtils.isNoneBlank(rpcService.forwardService())) {
            toService = rpcService.forwardService();
        }
        MessagerType toServiceType = MessagerTypes.checkGroup(toService);
        RpcInvokeNodeSet remoterSet = rpcRemoterManager.loadInvokeNodeSet(toServiceType);
        RpcRemoteInstance instance = new RpcRemoteInstance(rpcClass, this.setting, remoterSet);
        Map<Method, RpcRemoteInvoker> invokerMap = new HashMap<>();
        for (RpcRemoteMethod method : methods) {
            RpcRouter router = rpcRouteManager.getRouter(method.getRouterClass());
            if (router == null) {
                throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "调用 {} 异常, 未找到 {} RpcRouter",
                        method.getMethod(), method.getRouterClass());
            }
            RpcRemoteInvoker invoker = new RpcRemoteInvoker(instance, method, router, rpcMonitor);
            invokerMap.put(method.getMethod(), invoker);
        }
        instance.setInvokerMap(invokerMap);
        return instance;
    }

    public RpcRemoteInstanceFactory setSetting(RpcRemoteSetting setting) {
        this.setting = setting;
        return this;
    }

    public RpcRemoteInstanceFactory setRpcRemoterManager(RpcInvokeNodeManager rpcRemoterManager) {
        this.rpcRemoterManager = rpcRemoterManager;
        return this;
    }

    public RpcRemoteInstanceFactory setRpcRouteManager(RpcRouteManager rpcRouteManager) {
        this.rpcRouteManager = rpcRouteManager;
        return this;
    }

}
