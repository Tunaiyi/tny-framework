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
@TypeProtobuf(PB_BOOLEAN)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBBoolean {

    @Protobuf(fieldType = FieldType.BOOL)
    private boolean value;

    public PBBoolean() {
    }

    public PBBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public boolean isValue() {
        return this.value;
    }

    private PBBoolean setValue(boolean value) {
        this.value = value;
        return this;
    }

}
