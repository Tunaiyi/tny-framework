package com.tny.game.net.rpc;

import com.tny.game.net.transport.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class FirstRpcRemoteRouter implements RpcRemoteRouter<Object> {

    @Override
    public RpcRemoteAccessPoint route(RpcRemoteServiceSet servicer, RpcRemoteMethod invoker, Object routeValue, Object... params) {
        List<RpcRemoteNode> nodes = servicer.getOrderRemoteNodes();
        if (nodes.isEmpty()) {
            return null;
        }
        RpcRemoteNode node = nodes.get(0);
        List<RpcRemoteAccessPoint> accessPoints = node.getOrderAccessPoints();
        if (accessPoints.isEmpty()) {
            return null;
        }
        return accessPoints.get(0);
    }

}
