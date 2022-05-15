package com.tny.game.net.rpc;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 3:33 下午
 */
public interface RpcRemoteRouteManager {

    <T> RpcRemoteRouter<T> getRouter(Class<?> routerClass);

}
