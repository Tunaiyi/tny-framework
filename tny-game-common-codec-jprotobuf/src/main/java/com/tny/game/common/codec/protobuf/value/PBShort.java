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
@TypeProtobuf(PB_SHORT)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBShort {

    @Protobuf(fieldType = FieldType.INT32)
    private byte value;

    public PBShort() {
    }

    public PBShort(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    private PBShort setValue(byte value) {
        this.value = value;
        return this;
    }

}
