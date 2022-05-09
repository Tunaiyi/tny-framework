package com.tny.game.net.message;

/**
 * Rpc转发HeaderBuilder
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 02:37
 **/
public class RpcOriginalMessageIdHeaderBuilder extends MessageHeaderBuilder<RpcOriginalMessageIdHeader> {

    private RpcOriginalMessageIdHeaderBuilder() {
    }

    public static RpcOriginalMessageIdHeaderBuilder newBuilder() {
        return new RpcOriginalMessageIdHeaderBuilder();
    }

    @Override
    protected RpcOriginalMessageIdHeader create() {
        return new RpcOriginalMessageIdHeader();
    }

    public RpcOriginalMessageIdHeaderBuilder setMessageId(long messageId) {
        header().setMessageId(messageId);
        return this;
    }

}
