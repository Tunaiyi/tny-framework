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

    public long getMessageId() {
        return messageId;
    }

    RpcOriginalMessageIdHeader setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

}
