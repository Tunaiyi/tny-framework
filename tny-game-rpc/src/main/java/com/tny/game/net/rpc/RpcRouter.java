package com.tny.game.net.rpc;

import com.tny.game.net.endpoint.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public interface RpcRouter<T> {

	Endpoint<?> route(List<RpcRemoteNode> nodes, RpcMethod invoker, T routeValue, Object... params);

}
