package com.tny.game.net.demo.common.dto;

import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * <p>
 */

@ProtoEx(1000_01_01)
@DTODoc("说话DTO")
public class SayContentDTO {

    @ProtoExField(1)
    private long userId;

    @ProtoExField(2)
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
