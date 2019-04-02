package com.tny.game.net.demo.common.dto;

import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * <p>
 */

@ProtoEx(1000_01_00)
@DTODoc("登录DTO")
public class LoginDTO {

    @ProtoExField(1)
    private long userId;

    @ProtoExField(2)
    private String message;

    @ProtoExField(3)
    private long certId;

    public LoginDTO() {
    }

    public LoginDTO(long certId, long userId, String message) {
        this.userId = userId;
        this.certId = certId;
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public long getCertId() {
        return certId;
    }
}
