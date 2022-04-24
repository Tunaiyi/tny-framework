package com.tny.game.net.rpc;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

/**
 * Endpoint接入点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/24 14:27
 **/
public class RpcAccessEndpoint implements RpcAccessPoint {

    private final Endpoint<RpcAccessId> endpoint;

    public RpcAccessEndpoint(Endpoint<RpcAccessId> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public SendReceipt send(MessageContext messageContext) {
        return endpoint.send(messageContext);
    }

    @Override
    public RpcAccessId getAccessId() {
        return endpoint.getUserId();
    }

}
