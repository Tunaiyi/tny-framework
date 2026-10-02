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

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 4:23 下午
 */
public class DefaultRpcRouteManager implements RpcRouteManager {

    private final Class<?> defaultRouterClass;

    private final Map<Class<?>, RpcRouter> routerMap;

    public DefaultRpcRouteManager(Class<?> defaultRouterClass, Collection<RpcRouter> routers) {
        Map<Class<?>, RpcRouter> routerMap = new HashMap<>();
        for (RpcRouter router : routers) {
            routerMap.put(router.getClass(), router);
        }
        this.defaultRouterClass = defaultRouterClass;
        this.routerMap = ImmutableMap.copyOf(routerMap);
    }

    @Override
    public RpcRouter getRouter(Class<? extends RpcRouter> routerClass) {
        if (routerClass == null || RpcRouter.class == routerClass) {
            return as(this.routerMap.get(defaultRouterClass));
        }
        return as(this.routerMap.get(routerClass));
    }

}
