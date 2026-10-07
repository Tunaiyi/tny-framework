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
