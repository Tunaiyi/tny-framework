package com.tny.game.net.message;

/**
 * Header Id
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/29 16:22
 **/
public interface MessageHeaderConstants {

    int RPC_FORWARD_HEADER_TYPE_PROTO = 100;
    String RPC_FORWARD_HEADER_KEY = "Rpc-Forward";
    MessageHeaderKey<RpcForwardHeader> RPC_FORWARD_HEADER =
            MessageHeaderKey.key(RPC_FORWARD_HEADER_KEY, RpcForwardHeader.class);

    int RPC_ORIGINAL_MESSAGE_ID_TYPE_PROTO = 101;
    String RPC_ORIGINAL_MESSAGE_ID_KEY = "Rpc-Original-Message-Id";

    MessageHeaderKey<RpcOriginalMessageIdHeader> RPC_ORIGINAL_MESSAGE_ID =
            MessageHeaderKey.key(RPC_ORIGINAL_MESSAGE_ID_KEY, RpcOriginalMessageIdHeader.class);

}
