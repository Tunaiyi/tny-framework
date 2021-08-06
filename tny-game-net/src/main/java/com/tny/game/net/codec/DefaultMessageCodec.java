package com.tny.game.net.codec;

import com.tny.game.common.buff.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.protoex.*;

import java.nio.ByteBuffer;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.MessageType.*;
import static com.tny.game.net.message.codec.CodecConstants.*;

@Unit
public class DefaultMessageCodec implements MessageCodec {

    private final MessageBodyCodec<Object> bodyCoder;
    //    private final Codec<?> tailCoder;
    private final DecodeStrategy bodyDecodeStrategy;

    public DefaultMessageCodec(MessageBodyCodec<?> bodyCoder) {
        this(bodyCoder, null);
    }

    public DefaultMessageCodec(MessageBodyCodec<?> bodyCoder, DecodeStrategy bodyDecodeStrategy) {
        this.bodyCoder = as(bodyCoder);
        this.bodyDecodeStrategy = ObjectAide.ifNull(bodyDecodeStrategy, DecodeStrategy.DECODE_ALL_STRATEGY);
    }

    @Override
    public Message decode(byte[] bytes, MessageFactory messageFactory) throws Exception {
        ProtoExInputStream stream = new ProtoExInputStream(bytes);
        long id = stream.readLong();
        byte option = stream.readByte();
        MessageMode mode = MessageMode.valueOf(MESSAGE, (byte)(option & MESSAGE_HEAD_OPTION_MODE_MASK));
        int protocol = stream.readInt();
        int code = stream.readInt();
        long toMessage = stream.readLong();
        long time = stream.readLong();
        Object body = null;
        //        Object tail = null;

        int line = (byte)(option & MESSAGE_HEAD_OPTION_LINE_MASK);
        line = line >> MESSAGE_HEAD_OPTION_LINE_SHIFT;
        CommonMessageHead head = new CommonMessageHead(id, mode, line, protocol, code, toMessage, time);
        if (CodecConstants.isOption(option, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST)) {
            if (this.bodyDecodeStrategy.isNeedDecode(head)) {
                ByteBuffer buffer = stream.readBuffer();
                body = this.bodyCoder.decode(buffer);
            } else {
                body = new BodyBytes(stream.readBytes());
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
        option = (byte)(option | (message.existBody() ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST : (byte)0));
        int line = head.getLine();
        if (line < MESSAGE_HEAD_OPTION_LINE_VALUE_MIN || line > MESSAGE_HEAD_OPTION_LINE_VALUE_MAX) {
            throw CodecException.causeEncode("line is {}. line must {} <= line <= {}", line,
                    MESSAGE_HEAD_OPTION_LINE_VALUE_MIN, MESSAGE_HEAD_OPTION_LINE_VALUE_MAX);
        }
        option = (byte)(option | line << MESSAGE_HEAD_OPTION_LINE_SHIFT);
        stream.writeByte(option);
        stream.writeInt(head.getProtocolId());
        stream.writeInt(head.getCode());
        stream.writeLong(head.getToMessage());
        stream.writeLong(head.getTime());

        if (message.existBody()) {
            Object body = message.bodyAs(Object.class);
            writeObject(stream, body, this.bodyCoder);
        }
        return stream.toByteArray();
    }

    private void writeObject(ProtoExOutputStream stream, Object object, MessageBodyCodec<Object> coder) throws Exception {
        if (object instanceof byte[]) {
            stream.writeBytes((byte[])object);
        } else if (object instanceof BodyBytes) {
            byte[] bodyBytes = ((BodyBytes)object).getBodyBytes();
            stream.writeBytes(new LinkedByteBuffer(bodyBytes, 0, bodyBytes.length));
        } else {
            ByteBuffer buffer = coder.encode(as(object));
            stream.writeBytes(buffer.array(), buffer.arrayOffset(), buffer.limit());
        }
    }

}
