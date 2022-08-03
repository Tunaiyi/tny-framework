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
public class RpcRemoteServiceAccess implements RpcServiceAccess {

    private final Endpoint<RpcAccessIdentify> endpoint;

    private final ForwardPoint forwardPoint;

    public RpcRemoteServiceAccess(Endpoint<RpcAccessIdentify> endpoint) {
        this.endpoint = endpoint;
        this.forwardPoint = new ForwardPoint(endpoint.getUserId());
    }

    @Override
    public SendReceipt send(MessageContext messageContext) {
        return endpoint.send(messageContext);
    }

    @Override
    public long getAccessId() {
        return endpoint.getMessagerId();
    }

    @Override
    public Endpoint<RpcAccessIdentify> getEndpoint() {
        return endpoint;
    }

    @Override
    public RpcAccessIdentify getIdentify() {
        return endpoint.getUserId();
    }

    @Override
    public ForwardPoint getForwardPoint() {
        return forwardPoint;
    }

    @Override
    public boolean isActive() {
        return endpoint.isActive();
    }

}
