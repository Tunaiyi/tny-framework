package com.tny.game.net.rpc;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class FirstRpcRouter implements RpcRouter<Object> {

    @Override
    public RpcAccessPoint route(List<RpcRemoteNode> nodes, RpcMethod invoker, Object routeValue, Object... params) {
        if (nodes.isEmpty()) {
            return null;
        }
        RpcRemoteNode node = nodes.get(0);
        List<RpcAccessPoint> accessPoints = node.getOrderEndpoints();
        if (accessPoints.isEmpty()) {
            return null;
        }
        return accessPoints.get(0);
    }

}
