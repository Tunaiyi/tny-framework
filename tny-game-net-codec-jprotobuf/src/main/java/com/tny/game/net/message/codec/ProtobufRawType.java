package com.tny.game.net.message.codec;

import com.tny.game.common.enums.*;
import com.tny.game.common.type.*;
import com.tny.game.common.utils.*;
import com.tny.game.protoex.*;

import java.util.*;
import java.util.function.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 12:22 上午
 */

final class ClassTypeMapHolder {

    static final Map<Class<?>, ProtobufRawType> CLASS_TYPE_MAP = new HashMap<>();

}

public enum ProtobufRawType implements EnumIdentifiable<Byte> {

    NULL(PROTOBUF_RAW_TYPE_ID_NULL, null, null, null),

    BYTE(PROTOBUF_RAW_TYPE_ID_BYTE, Byte.class, ProtoExOutputStream::writeByte, ProtoExInputStream::readByte),

    //    BYTE_LIST(PROTOBUF_RAW_TYPE_ID_BYTE_LIST, byte[].class, null, null),

    SHORT(PROTOBUF_RAW_TYPE_ID_SHORT, Short.class, ProtoExOutputStream::writeShort, ProtoExInputStream::readShort),

    //    SHORT_LIST(PROTOBUF_RAW_TYPE_ID_SHORT_LIST, short[].class, null, null),

    INT(PROTOBUF_RAW_TYPE_ID_INT, Integer.class, ProtoExOutputStream::writeInt, ProtoExInputStream::readInt),

    //    INT_LIST(PROTOBUF_RAW_TYPE_ID_INT_LIST, int[].class, null, null),

    LONG(PROTOBUF_RAW_TYPE_ID_LONG, Long.class, ProtoExOutputStream::writeLong, ProtoExInputStream::readLong),

    //    LONG_LIST(PROTOBUF_RAW_TYPE_ID_LONG_LIST, long[].class, null, null),

    FLOAT(PROTOBUF_RAW_TYPE_ID_FLOAT, Float.class, ProtoExOutputStream::writeFloat, ProtoExInputStream::readFloat),

    //    FLOAT_LIST(PROTOBUF_RAW_TYPE_ID_FLOAT_LIST, float[].class, null, null),

    DOUBLE(PROTOBUF_RAW_TYPE_ID_DOUBLE, Double.class, ProtoExOutputStream::writeDouble, ProtoExInputStream::readDouble),

    //    DOUBLE_LIST(PROTOBUF_RAW_TYPE_ID_DOUBLE_LIST, double[].class, null, null),

    BOOL(PROTOBUF_RAW_TYPE_ID_BOOL, Boolean.class, ProtoExOutputStream::writeBoolean, ProtoExInputStream::readBoolean),

    //    BOOL_LIST(PROTOBUF_RAW_TYPE_ID_BOOL_LIST, boolean[].class, null, null),

    STRING(PROTOBUF_RAW_TYPE_ID_STRING, String.class, ProtoExOutputStream::writeString, ProtoExInputStream::readString),

    //    STRING_LIST(PROTOBUF_RAW_TYPE_ID_STRING_LIST, String[].class, null, null),

    COMPLEX(PROTOBUF_RAW_TYPE_ID_COMPLEX, Object.class, null, null),

    //
    ;

    private final byte id;

    private BiConsumer<ProtoExOutputStream, Object> valueWriter;

    private Function<ProtoExInputStream, Object> valueReader;

    public Class<?> classTypes;

    <T> ProtobufRawType(byte id, Class<T> classTypes,
            BiConsumer<ProtoExOutputStream, T> valueWriter,
            Function<ProtoExInputStream, Object> valueReader) {
        this.id = id;
        this.classTypes = classTypes;
        this.valueWriter = as(valueWriter);
        this.valueReader = as(valueReader);
        ProtobufRawType old = ClassTypeMapHolder.CLASS_TYPE_MAP.put(classTypes, this);
        ThrowAide.checkArgument(old == null,
                "{} 与 {} 都关联 Class {}", this, old, classTypes);
    }

    ProtobufRawType(ProtobufRawType type, byte option) {
        this.id = (byte)(type.getId() | option);
    }

    @Override
    public Byte getId() {
        return this.id;
    }

    public byte option() {
        return this.id;
    }

    public void writeValue(ProtoExOutputStream output, Object value) {
        if (this.valueWriter != null) {
            this.valueWriter.accept(output, value);
        }
    }

    public Object readerValue(ProtoExInputStream input) {
        if (this.valueReader != null) {
            return this.valueReader.apply(input);
        }
        return null;
    }

    public boolean isHasValueReader() {
        return this.valueReader != null;
    }

    public boolean isHasValueWriter() {
        return this.valueWriter != null;
    }

    public static ProtobufRawType ofObject(Object object) {
        if (object == null) {
            return ProtobufRawType.NULL;
        }
        Class<?> clazz = object.getClass();
        if (clazz.isPrimitive()) {
            clazz = Wrapper.getWrapper(clazz);
        }
        return ClassTypeMapHolder.CLASS_TYPE_MAP.getOrDefault(clazz, ProtobufRawType.COMPLEX);
    }

}
