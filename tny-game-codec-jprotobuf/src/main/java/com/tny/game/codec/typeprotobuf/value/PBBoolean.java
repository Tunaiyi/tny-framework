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
@TypeProtobuf(PB_BOOLEAN)
@Codable(ProtobufMimeType.PROTOBUF)
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
