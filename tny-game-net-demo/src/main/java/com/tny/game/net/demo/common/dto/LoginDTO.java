package com.tny.game.net.demo.common.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.protoex.annotations.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:49
 */

@ProtoEx(1000_01_00)
@DTODoc("登录DTO")
public class LoginDTO {

    @ProtoExField(1)
    private long userId;

    @ProtoExField(2)
    private String  message;

    public LoginDTO() {
    }

    public LoginDTO(long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

}
