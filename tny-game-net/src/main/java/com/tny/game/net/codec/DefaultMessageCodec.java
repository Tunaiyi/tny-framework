package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.protoex.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.MessageType.*;
import static com.tny.game.net.message.codec.CodecConstants.*;

@Unit
public class DefaultMessageCodec implements MessageCodec {

    private final MessageBodyCodec<?> bodyCoder;
    //    private final Codec<?> tailCoder;
    private final DecodeStrategy bodyDecodeStrategy;

    public DefaultMessageCodec(MessageBodyCodec<?> bodyCoder) {
        this(bodyCoder, null);
    }

    public DefaultMessageCodec(MessageBodyCodec<?> bodyCoder, DecodeStrategy bodyDecodeStrategy) {
        this.bodyCoder = bodyCoder;
        this.bodyDecodeStrategy = ObjectAide.ifNull(bodyDecodeStrategy, DecodeStrategy.DECODE_ALL_STRATEGY);
    }

    @Override
    public Message decode(byte[] bytes, MessageFactory messageFactory) throws Exception {
        ProtoExInputStream stream = new ProtoExInputStream(bytes);
        long id = stream.readLong();
        byte option = stream.getBuffer().get();
        MessageMode mode = MessageMode.valueOf(MESSAGE, (byte)(option & MESSAGE_HEAD_OPTION_MODE_MASK));
        int protocol = stream.readInt();
        int code = stream.readInt();
        long toMessage = stream.readLong();
        long time = stream.readLong();
        Object body = null;
        //        Object tail = null;
        byte[] bodyBytes = null;

        if (CodecConstants.isOption(option, MESSAGE_HEAD_OPTION_EXIST_BODY)) {
            bodyBytes = stream.readBytes();
        }

        int line = (byte)(option & MESSAGE_HEAD_OPTION_LINE_MASK);
        CommonMessageHead head = new CommonMessageHead(id, mode, line, protocol, code, toMessage, time);
        if (bodyBytes != null) {
            if (this.bodyDecodeStrategy.isNeedDecode(head)) {
                body = this.bodyCoder.decode(bodyBytes);
            } else {
                body = new BodyBytes(bodyBytes);
            }
        }
        return messageFactory.create(head, body);
    }

    @Override
    public byte[] encode(Message message) throws Exception {
        if (message.getMode().getType() != MESSAGE) {
            return new byte[0];
        }
        ProtoExOutputStream stream = new ProtoExOutputStream(256, 128);
        stream.writeLong(message.getId());
        MessageHead head = message.getHead();
        MessageMode mode = head.getMode();
        byte option = mode.getOption();
        option = (byte)(option | (message.existBody() ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_BODY : (byte)0));
        int line = head.getLine();
        if (line < MESSAGE_HEAD_OPTION_LINE_MIN_VALUE || line > MESSAGE_HEAD_OPTION_LINE_MAX_VALUE) {
            throw CodecException.causeEncode("line is {}. line must {} <= line <= {}", line,
                    MESSAGE_HEAD_OPTION_LINE_MIN_VALUE, MESSAGE_HEAD_OPTION_LINE_MAX_VALUE);
        }
        option = (byte)(option | line << MESSAGE_HEAD_OPTION_LINE_SHIFT);
        stream.getByteBuffer().write(option);
        stream.writeInt(head.getProtocolId());
        stream.writeInt(head.getCode());
        stream.writeLong(head.getToMessage());
        stream.writeLong(head.getTime());
        if (message.existBody()) {
            Object body = message.getBody(Object.class);
            writeObject(stream, body, this.bodyCoder);
        }
        //        if (message.existTail()) {
        //            Object tail = message.getTail(Object.class);
        //            writeObject(stream, tail, this.tailCoder);
        //        }
        return stream.toByteArray();
    }

    private void writeObject(ProtoExOutputStream stream, Object object, MessageBodyCodec<?> coder) throws Exception {
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
