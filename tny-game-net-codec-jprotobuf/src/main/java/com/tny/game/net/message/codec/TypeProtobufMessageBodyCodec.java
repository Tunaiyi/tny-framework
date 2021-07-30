package com.tny.game.net.message.codec;

import com.tny.game.codec.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.protoex.*;

import java.nio.ByteBuffer;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/11 11:13 下午
 */
@Unit
public class TypeProtobufMessageBodyCodec<T> implements MessageBodyCodec<T> {

    private final TypeProtobufObjectCodecFactory codecFactory;

    public TypeProtobufMessageBodyCodec() {
        this.codecFactory = new TypeProtobufObjectCodecFactory();
    }

    public TypeProtobufMessageBodyCodec(TypeProtobufObjectCodecFactory codecFactory) {
        this.codecFactory = codecFactory;
    }

    @Override
    public T decode(ByteBuffer buffer) throws Exception {
        ProtoExInputStream input = new ProtoExInputStream(buffer);
        List<Object> paramList = null;
        Object body = null;
        while (input.hasRemaining()) {
            byte option = input.readByte();
            if ((option & PROTOBUF_MESSAGE_PARAMS) != 0 && paramList == null) {
                paramList = new ArrayList<>();
                body = new MessageParamList(paramList);
            }
            ProtobufRawType rawType = EnumAide.of(ProtobufRawType.class, (byte)(option & PROTOBUF_HEAD_TYPE_BIT_MASK));
            Object value = doDecode(rawType, input);
            if (paramList != null) {
                paramList.add(value);
            } else {
                body = value;
            }
        }
        return as(body);
    }

    private Object doDecode(ProtobufRawType rawType, ProtoExInputStream input) throws Exception {
        if (rawType.isHasValueReader()) {
            return rawType.readerValue(input);
        } else {
            ObjectCodec<T> codec = this.codecFactory.createCodec(null);
            return codec.decodeByBytes(input.readBytes());
        }
    }

    @Override
    public ByteBuffer encode(T object) throws Exception {
        ProtoExOutputStream output = new ProtoExOutputStream();
        if (object instanceof MessageParamList) {
            for (Object param : ((MessageParamList)object)) {
                doEncode(output, param, true);
            }
        } else {
            doEncode(output, object, false);
        }
        return ByteBuffer.wrap(output.toByteArray());
    }

    private void doEncode(ProtoExOutputStream output, Object object, boolean params) throws Exception {
        byte option = params ? PROTOBUF_MESSAGE_PARAMS : 0;
        if (object == null) {
            output.writeByte((byte)(option | ProtobufRawType.NULL.option()));
            return;
        }
        ProtobufRawType rawType = ProtobufRawType.ofObject(object);
        output.writeByte((byte)(option | rawType.option()));
        if (rawType.isHasValueWriter()) {
            rawType.writeValue(output, object);
        } else {
            ObjectCodec<Object> codec = this.codecFactory.createCodec(null);
            byte[] objectBytes = codec.encodeToBytes(object);
            output.writeBytes(objectBytes);
        }
    }

}
