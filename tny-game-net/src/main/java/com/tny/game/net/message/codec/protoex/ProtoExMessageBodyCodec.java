package com.tny.game.net.message.codec.protoex;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;

import java.nio.ByteBuffer;

@Unit
public class ProtoExMessageBodyCodec<T> implements MessageBodyCodec<T> {

    @Override
    public T decode(ByteBuffer bodyBuffer) {
        ProtoExReader bodyReader = new ProtoExReader(new ProtoExInputStream(bodyBuffer));
        return bodyReader.readMessage();
    }

    @Override
    public ByteBuffer encode(T object) {
        ProtoExWriter writer = new ProtoExWriter();
        if (object != null) {
            writer.writeMessage(object, TypeEncode.EXPLICIT);
        }
        return ByteBuffer.wrap(writer.toByteArray());
    }

}
