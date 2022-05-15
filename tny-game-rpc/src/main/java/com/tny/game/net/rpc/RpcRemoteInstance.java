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
public class RpcRemoteInstance {

    private final Class<?> rpcClass;

    private final RpcRemoteSetting setting;

    private final RpcRemoteServiceSet serviceSet;

    private Map<Method, RpcRemoteInvoker> invokerMap = ImmutableMap.of();

    public RpcRemoteInstance(Class<?> rpcClass, RpcRemoteSetting setting, RpcRemoteServiceSet serviceSet) {
        this.rpcClass = rpcClass;
        this.setting = setting;
        this.serviceSet = serviceSet;
    }

    public Class<?> getRpcClass() {
        return rpcClass;
    }

    public RpcRemoteSetting getSetting() {
        return setting;
    }

    public RpcRemoteServiceSet getServiceSet() {
        return serviceSet;
    }

    public RpcRemoteInvoker invoker(Method method) {
        return invokerMap.get(method);
    }

    RpcRemoteInstance setInvokerMap(Map<Method, RpcRemoteInvoker> invokerMap) {
        this.invokerMap = ImmutableMap.copyOf(invokerMap);
        return this;
    }

}
