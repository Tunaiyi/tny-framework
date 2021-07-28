package com.tny.game.codec.protobuf.value;

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
@TypeProtobuf(PB_DOUBLE)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBDouble extends Number {

    @Protobuf(fieldType = FieldType.DOUBLE)
    private double value;

    public PBDouble() {
    }

    public PBDouble(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    private PBDouble setValue(double value) {
        this.value = value;
        return this;
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return (long)this.value;
    }

    @Override
    public float floatValue() {
        return (float)this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

}
