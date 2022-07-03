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
