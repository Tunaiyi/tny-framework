package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 3:23 下午
 */
public class RpcInstance {

    private final Class<?> rpcClass;

    private final RpcSetting setting;

    private final RpcRemoteServiceSet servicer;

    private final RpcRouteManager rpcRouteManager;

    private Map<Method, RpcInvoker> invokerMap = ImmutableMap.of();

    public RpcInstance(Class<?> rpcClass, RpcSetting setting, RpcRemoteServiceSet servicer, RpcRouteManager rpcRouteManager) {
        this.rpcClass = rpcClass;
        this.setting = setting;
        this.servicer = servicer;
        this.rpcRouteManager = rpcRouteManager;
    }

    public Class<?> getRpcClass() {
        return rpcClass;
    }

    public RpcSetting getSetting() {
        return setting;
    }

    public RpcRemoteServiceSet getServicer() {
        return servicer;
    }

    public <T> RpcRouter<T> getRouter(Class<?> routerClass) {
        return rpcRouteManager.getRouter(routerClass);
    }

    public Map<Method, RpcInvoker> getInvokerMap() {
        return invokerMap;
    }

    public RpcInvoker invoker(Method method) {
        return invokerMap.get(method);
    }

    RpcInstance setInvokerMap(Map<Method, RpcInvoker> invokerMap) {
        this.invokerMap = ImmutableMap.copyOf(invokerMap);
        return this;
    }

}
