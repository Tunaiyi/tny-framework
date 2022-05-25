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
public class DefaultRpcRemoteRouteManager implements RpcRemoteRouteManager {

    private final Class<?> defaultRouterClass;

    private final Map<Class<?>, RpcRemoteRouter> routerMap;

    public DefaultRpcRemoteRouteManager(Class<?> defaultRouterClass, Collection<RpcRemoteRouter> routers) {
        Map<Class<?>, RpcRemoteRouter> routerMap = new HashMap<>();
        for (RpcRemoteRouter router : routers) {
            routerMap.put(router.getClass(), router);
        }
        this.defaultRouterClass = defaultRouterClass;
        this.routerMap = ImmutableMap.copyOf(routerMap);
    }

    @Override
    public RpcRemoteRouter getRouter(Class<? extends RpcRemoteRouter> routerClass) {
        if (routerClass == null || RpcRemoteRouter.class == routerClass) {
            return as(this.routerMap.get(defaultRouterClass));
        }
        return as(this.routerMap.get(routerClass));
    }

}
