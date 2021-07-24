package com.tny.game.common.codec.protobuf.value;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.common.codec.annotation.*;
import com.tny.game.common.codec.protobuf.*;
import com.tny.game.common.codec.typeprotobuf.annotation.*;

import static com.tny.game.common.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-04-01 15:30
 */
@ProtobufClass
@TypeProtobuf(PB_DOUBLE)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBDouble {

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

}
