package com.tny.game.net.rpc;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class FirstRpcRouter implements RpcRouter {

    @Override
    public RpcRemoterAccess route(RpcRemoterSet servicer, RpcRemoteMethod invoker, RpcRemoteInvokeParams invokeParams) {
        List<? extends RpcRemoterNode> nodes = servicer.getOrderRemoterNodes();
        if (nodes.isEmpty()) {
            return null;
        }
        RpcRemoterNode node = nodes.get(0);
        List<? extends RpcRemoterAccess> accessPoints = node.getOrderRemoterAccesses();
        if (accessPoints.isEmpty()) {
            return null;
        }
        return accessPoints.get(0);
    }

}
