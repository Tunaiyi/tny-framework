package com.tny.game.net.rpc;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * Endpoint接入点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/24 14:27
 **/
public class RpcEndpointAccessPoint implements RpcRemoteAccessPoint {

    private final Endpoint<RpcAccessIdentify> endpoint;

    private final ForwardRpcServicer forwardRpcServicer;

    public RpcEndpointAccessPoint(Endpoint<RpcAccessIdentify> endpoint) {
        this.endpoint = endpoint;
        this.forwardRpcServicer = new ForwardRpcServicer(endpoint.getUserId());
    }

    @Override
    public SendReceipt send(MessageContext messageContext) {
        return endpoint.send(messageContext);
    }

    @Override
    public RpcAccessIdentify getAccessId() {
        return endpoint.getUserId();
    }

    public Endpoint<RpcAccessIdentify> getEndpoint() {
        return endpoint;
    }

    @Override
    public ForwardRpcServicer getForwardRpcServicer() {
        return forwardRpcServicer;
    }

}
