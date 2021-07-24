package com.tny.game.common.codec.protobuf.value;

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
@TypeProtobuf(PB_STRING)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBString {

    @Protobuf
    private String value;

    public PBString() {
    }

    public PBString(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    private PBString setValue(String value) {
        this.value = value;
        return this;
    }

}
