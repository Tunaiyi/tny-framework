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
