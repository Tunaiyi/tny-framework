package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public class FirstRpcForwarderStrategy implements RpcForwarderStrategy {

    @Override
    public RpcRemoteAccessPoint forward(RpcRemoteServiceSet serviceSet, Message message,
            RpcServicer from, Messager sender, RpcServicer to, Messager receiver) {
        return serviceSet.getOrderRemoteNodes().stream()
                .findFirst()
                .flatMap(node -> node.getOrderEndpoints().stream().findFirst())
                .orElse(null);
    }

}
