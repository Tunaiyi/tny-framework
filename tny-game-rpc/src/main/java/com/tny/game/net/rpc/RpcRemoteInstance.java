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

    private final RpcInvokeNodeSet serviceSet;

    private Map<Method, RpcRemoteInvoker> invokerMap = ImmutableMap.of();

    public RpcRemoteInstance(Class<?> rpcClass, RpcRemoteSetting setting, RpcInvokeNodeSet serviceSet) {
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

    public RpcInvokeNodeSet getServiceSet() {
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
