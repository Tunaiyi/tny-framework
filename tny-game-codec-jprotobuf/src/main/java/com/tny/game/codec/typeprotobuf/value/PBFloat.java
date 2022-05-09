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
@TypeProtobuf(PB_FLOAT)
@Codable(ProtobufMimeType.PROTOBUF)
public class PBFloat extends Number {

    @Protobuf(fieldType = FieldType.FLOAT)
    private float value;

    public PBFloat() {
    }

    public PBFloat(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    private PBFloat setValue(float value) {
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
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

}
