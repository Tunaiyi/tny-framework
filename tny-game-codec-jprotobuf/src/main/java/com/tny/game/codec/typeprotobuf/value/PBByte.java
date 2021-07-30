package com.tny.game.codec.typeprotobuf.value;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import static com.tny.game.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-04-01 15:30
 */
@ProtobufClass
@TypeProtobuf(PB_BYTE)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBByte extends Number {

    @Protobuf(fieldType = FieldType.INT32)
    private int value;

    public PBByte() {
    }

    public PBByte(byte value) {
        this.setValue(value);
    }

    public int getValue() {
        return (byte)this.value;
    }

    private PBByte setValue(int value) {
        this.value = (byte)value;
        return this;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

}
