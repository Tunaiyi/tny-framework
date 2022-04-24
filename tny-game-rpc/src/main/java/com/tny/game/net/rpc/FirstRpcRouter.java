package com.tny.game.net.rpc;

import com.tny.game.net.endpoint.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class FirstRpcRouter implements RpcRouter<Object> {

    @Override
    public Endpoint<?> route(List<RpcRemoteNode> nodes, RpcMethod invoker, Object routeValue, Object... params) {
        if (nodes.isEmpty()) {
            return null;
        }
        RpcRemoteNode node = nodes.get(0);
        List<Endpoint<RpcLinkerId>> endpoints = node.getOrderEndpoints();
        if (endpoints.isEmpty()) {
            return null;
        }
        return endpoints.get(0);
    }

}
