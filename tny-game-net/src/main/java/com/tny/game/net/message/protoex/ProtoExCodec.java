package com.tny.game.net.message.protoex;

import com.tny.game.common.buff.LinkedByteBuffer;
import com.tny.game.common.unit.annotation.Unit;
import com.tny.game.net.message.*;
import com.tny.game.net.message.coder.Codec;
import com.tny.game.net.message.common.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.TypeEncode;

import java.util.Arrays;

import static com.tny.game.common.utils.ObjectAide.*;

@Unit
public class ProtoExCodec<T> implements Codec<Message<T>> {

    public final static int MESSAGE_HEAD_ID = 1;
    public final static int MESSAGE_BODY_ID = 2;

    private MessageFactory<T> messageFactory;

    public ProtoExCodec() {
        this(null);
    }

    public ProtoExCodec(MessageFactory<T> messageFactory) {
        this.messageFactory = ifNullAndGet(messageFactory, CommonMessageFactory::new);
    }

    protected boolean isDecode(MessageHeader header) {
        return true;
    }

    @Override
    public Message<T> decode(byte[] bytes) {
        ProtoExReader bodyReader = new ProtoExReader(bytes);
        NetMessageHeader header = bodyReader.readMessage(ProtoExMessageHeader.class);
        Object body = null;
        if (bodyReader.isCanRead()) {
            if (isDecode(header)) {
                body = bodyReader.readMessage();
            } else {
                ProtoExInputStream inputStream = bodyReader.getInputStream();
                int index = inputStream.position();
                body = new BodyBytes(Arrays.copyOfRange(bytes, index, bytes.length));
            }
        }
        return messageFactory.create(header, body);
    }

    @Override
    public byte[] encode(Message<T> message) {
        ProtoExWriter writer = new ProtoExWriter();
        writer.writeMessage(message.getHeader(), TypeEncode.EXPLICIT);
        Object body = message.getBody(Object.class);
        if (body != null) {
            if (body instanceof BodyBytes) {
                LinkedByteBuffer buffer = writer.getByteBuffer();
                buffer.write(((BodyBytes) body).getBodyBytes());
                return buffer.toByteArray();
            } else {
                writer.writeMessage(body, TypeEncode.EXPLICIT);
                return writer.toByteArray();
            }
        }
        return writer.toByteArray();
    }

}
