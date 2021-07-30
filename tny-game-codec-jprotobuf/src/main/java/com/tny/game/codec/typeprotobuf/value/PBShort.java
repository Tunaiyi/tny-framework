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
@TypeProtobuf(PB_SHORT)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBShort extends Number {

    @Protobuf(fieldType = FieldType.INT32)
    private int value;

    public PBShort() {
    }

    public PBShort(short value) {
        this.value = value;
    }

    public int getValue() {
        return (short)this.value;
    }

    private PBShort setValue(int value) {
        this.value = (short)value;
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
