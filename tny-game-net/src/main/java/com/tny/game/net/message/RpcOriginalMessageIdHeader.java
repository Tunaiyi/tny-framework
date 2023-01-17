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
