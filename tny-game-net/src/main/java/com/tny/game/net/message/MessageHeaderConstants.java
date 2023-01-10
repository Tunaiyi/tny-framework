/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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

    int RPC_TRACING_TYPE_PROTO = 102;

    String RPC_TRACING_TYPE_PROTO_KEY = "Rpc-Tracing";
    MessageHeaderKey<RpcTracingHeader> RPC_TRACING =
            MessageHeaderKey.key(RPC_TRACING_TYPE_PROTO_KEY, RpcTracingHeader.class);

}
