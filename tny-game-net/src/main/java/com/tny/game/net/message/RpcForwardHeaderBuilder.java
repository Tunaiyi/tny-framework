package com.tny.game.net.message;

import com.tny.game.net.base.*;

/**
 * Rpc转发HeaderBuilder
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 02:37
 **/
public class RpcForwardHeaderBuilder extends MessageHeaderBuilder<RpcForwardHeader> {

    private RpcForwardHeaderBuilder() {
    }

    public static RpcForwardHeaderBuilder newBuilder() {
        return new RpcForwardHeaderBuilder();
    }

    @Override
    protected RpcForwardHeader create() {
        return new RpcForwardHeader();
    }

    public RpcForwardHeaderBuilder setFrom(RpcServicer fromService) {
        header().setFrom(fromService);
        return this;
    }

    public RpcForwardHeaderBuilder setSender(Messager sender) {
        header().setSender(sender);
        return this;
    }

    public RpcForwardHeaderBuilder setTo(RpcServicer toServicer) {
        header().setTo(toServicer);
        return this;
    }

    public RpcForwardHeaderBuilder setReceiver(Messager receiver) {
        header().setReceiver(receiver);
        return this;
    }

    public RpcForwardHeaderBuilder setFromForwarder(RpcServicer fromService) {
        header().setFrom(fromService);
        return this;
    }

    public RpcForwardHeaderBuilder setToForwarder(RpcServicer toServicer) {
        header().setTo(toServicer);
        return this;
    }

}
