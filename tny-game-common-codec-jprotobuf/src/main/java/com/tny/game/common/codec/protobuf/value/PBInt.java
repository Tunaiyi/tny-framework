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
@TypeProtobuf(PB_INT)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBInt {

    @Protobuf(fieldType = FieldType.INT32)
    private int value;

    public PBInt() {
    }

    public PBInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private PBInt setValue(int value) {
        this.value = value;
        return this;
    }

}
