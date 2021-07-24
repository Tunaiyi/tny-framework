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
@TypeProtobuf(PB_BYTE)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBByte {

    @Protobuf(fieldType = FieldType.INT32)
    private byte value;

    public PBByte() {
    }

    public PBByte(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    private PBByte setValue(byte value) {
        this.value = value;
        return this;
    }

}
