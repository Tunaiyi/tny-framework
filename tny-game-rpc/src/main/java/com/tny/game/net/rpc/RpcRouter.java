package com.tny.game.net.rpc;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public interface RpcRouter<T> {

    RpcAccessPoint route(List<RpcRemoteNode> nodes, RpcMethod invoker, T routeValue, Object... params);

}
