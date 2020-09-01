package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.coder.*;
import com.tny.game.net.message.common.*;
import com.tny.game.protoex.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.MessageType.*;
import static com.tny.game.net.message.coder.CodecContent.*;

@Unit
public class DefaultMessageCodec<UID> implements MessageCodec<UID> {

    private final Codec<?> bodyCoder;
    private final Codec<?> tailCoder;
    private final DecodeStrategy bodyDecodeStrategy;

    public DefaultMessageCodec(Codec<?> bodyCoder, Codec<?> tailCoder, DecodeStrategy bodyDecodeStrategy) {
        this.bodyCoder = bodyCoder;
        this.tailCoder = tailCoder;
        this.bodyDecodeStrategy = ObjectAide.ifNull(bodyDecodeStrategy, DecodeStrategy.DECODE_ALL_STRATEGY);
    }

    @Override
    public Message<UID> decode(byte[] bytes, MessageFactory<UID> messageFactory) throws Exception {
        ProtoExInputStream stream = new ProtoExInputStream(bytes);
        long id = stream.readLong();
        byte option = stream.getBuffer().get();
        MessageMode mode = MessageMode.valueOf(MESSAGE, (byte)(option & HEAD_OPTION_MODE_MASK));
        int protocol = stream.readInt();
        int code = stream.readInt();
        long toMessage = stream.readLong();
        long time = stream.readLong();
        Object body = null;
        Object tail = null;
        byte[] bodyBytes = null;

        if (CodecContent.isOption(option, HEAD_OPTION_EXIST_BODY)) {
            bodyBytes = stream.readBytes();
        }
        if (CodecContent.isOption(option, HEAD_OPTION_EXIST_TAIL)) {
            byte[] tailBytes = stream.readBytes();
            tail = this.tailCoder.decode(tailBytes);
        }
        CommonMessageHead head = new CommonMessageHead(id, mode, protocol, code, toMessage, time);
        if (bodyBytes != null) {
            if (this.bodyDecodeStrategy.isNeedDecode(head, tail)) {
                body = this.bodyCoder.decode(bodyBytes);
            } else {
                body = new BodyBytes(bodyBytes);
            }
        }
        return messageFactory.create(head, body, tail);
    }

    @Override
    public byte[] encode(Message<UID> message) throws Exception {
        if (message.getMode().getType() != MESSAGE) {
            return new byte[0];
        }
        ProtoExOutputStream stream = new ProtoExOutputStream(256, 128);
        stream.writeLong(message.getId());
        MessageMode mode = message.getMode();
        byte option = mode.getOption();
        option = (byte)(option | (message.existBody() ? CodecContent.HEAD_OPTION_EXIST_BODY : (byte)0));
        option = (byte)(option | (message.existTail() ? CodecContent.HEAD_OPTION_EXIST_TAIL : (byte)0));
        stream.getByteBuffer().write(option);
        MessageHead head = message.getHead();
        stream.writeInt(message.getProtocolNumber());
        stream.writeInt(head.getCode());
        stream.writeLong(head.getToMessage());
        stream.writeLong(head.getTime());
        if (message.existBody()) {
            Object body = message.getBody(Object.class);
            writeObject(stream, body, this.bodyCoder);
        }
        if (message.existTail()) {
            Object tail = message.getTail(Object.class);
            writeObject(stream, tail, this.tailCoder);
        }
        return stream.toByteArray();
    }

    private void writeObject(ProtoExOutputStream stream, Object object, Codec<?> coder) throws Exception {
        if (object instanceof byte[]) {
            stream.writeBytes((byte[])object);
        } else if (object instanceof BodyBytes) {
            stream.writeBytes(((BodyBytes)object).getBodyBytes());
        } else {
            byte[] bodyBytes = coder.encode(as(object));
            stream.writeBytes(bodyBytes);
        }
    }

}
