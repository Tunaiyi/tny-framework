package com.tny.game.net.message.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import io.netty.buffer.ByteBuf;

@Unit
public class ProtoExMessageBodyCodec<T> implements MessageBodyCodec<T> {

    @Override
    public T decode(ByteBuf buffer) {
        try (ProtoExReader bodyReader = new ProtoExReader(new ProtoExInputStream(buffer.nioBuffer()))) {
            return bodyReader.readMessage();
        }
    }

    @Override
    public void encode(T object, ByteBuf code) {
        try (ProtoExWriter writer = new ProtoExWriter()) {
            if (object != null) {
                writer.writeMessage(object, TypeEncode.EXPLICIT);
            }
            code.writeBytes(writer.toByteArray());
        }
    }

}
