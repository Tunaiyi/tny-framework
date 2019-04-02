package com.tny.game.net.message.protoex;

import com.tny.game.common.buff.*;
import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.message.coder.*;
import com.tny.game.net.message.common.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;

@Unit
public class ProtoExCodec<T> implements Codec<T> {

    @Override
    public T decode(byte[] bytes) {
        ProtoExReader bodyReader = new ProtoExReader(bytes);
        return bodyReader.readMessage();
    }

    @Override
    public byte[] encode(T object) {
        ProtoExWriter writer = new ProtoExWriter();
        if (object != null) {
            if (object instanceof BodyBytes) {
                LinkedByteBuffer buffer = writer.getByteBuffer();
                buffer.write(((BodyBytes) object).getBodyBytes());
            } else {
                writer.writeMessage(object, TypeEncode.EXPLICIT);
            }
        }
        return writer.toByteArray();
    }

}
