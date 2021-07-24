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
@TypeProtobuf(PB_FLOAT)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBFloat {

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

}
