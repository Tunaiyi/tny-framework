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

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import static com.tny.game.net.message.MessageHeaderConstants.*;

/**
 * 原始MessageId
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 04:22
 **/
@TypeProtobuf(RPC_ORIGINAL_MESSAGE_ID_TYPE_PROTO)
@Codable(TypeProtobufMimeType.TYPE_PROTOBUF)
@ProtobufClass
public class RpcOriginalMessageIdHeader extends MessageHeader<RpcOriginalMessageIdHeader> {

    @Protobuf(order = 1)
    private long messageId;

    @Override
    public String getKey() {
        return RPC_ORIGINAL_MESSAGE_ID_KEY;
    }

    @Override
    public boolean isTransitive() {
        return false;
    }

    public long getMessageId() {
        return messageId;
    }

    RpcOriginalMessageIdHeader setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

}
