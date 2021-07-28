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
@TypeProtobuf(PB_LONG)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBLong {

    @Protobuf(fieldType = FieldType.INT64)
    private long value;

    public PBLong() {
    }

    public PBLong(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    private PBLong setValue(long value) {
        this.value = value;
        return this;
    }

}
