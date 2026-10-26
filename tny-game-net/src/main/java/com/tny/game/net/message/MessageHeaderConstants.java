/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
