package com.tny.game.net.demo.common.dto;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * <p>
 */

@DTODoc("说话DTO")
@ProtoEx(1000_01_01)
@ProtobufClass
@TypeProtobuf(1000_01_01)
public class SayContentDTO {

    @ProtoExField(1)
    @Protobuf(order = 1)
    private long userId;

    @ProtoExField(2)
    @Protobuf(order = 2)
    private String message;

    public SayContentDTO() {
    }

    public SayContentDTO(long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public long getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "SayContentDTO[\"message\" : \"" + this.message + "\"]";
    }

}
