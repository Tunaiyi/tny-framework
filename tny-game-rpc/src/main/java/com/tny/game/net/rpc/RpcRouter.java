package com.tny.game.net.rpc;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public interface RpcRouter<T> {

    RpcRemoteAccessPoint route(RpcRemoteServiceSet servicer, RpcMethod invoker, T routeValue, Object... params);

}
