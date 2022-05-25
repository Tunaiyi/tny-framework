package com.tny.game.net.transport;

import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
public class FirstRpcForwarderStrategy implements RpcForwardStrategy {

    @Override
    public RpcForwardAccess forward(RpcForwardSet forwarderSet, Message message, RpcForwardHeader forwardHeader) {
        return forwarderSet.getOrderForwarderNodes().stream()
                .findFirst()
                .flatMap(node -> node.getOrderForwardAccess().stream().findFirst())
                .orElse(null);
    }

}
