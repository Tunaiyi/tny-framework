package com.tny.game.demo.core.common.dto;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 2:49 下午
 */
@ProtobufClass
@TypeProtobuf(1000_00_00)
public class LoginResultDTO {

    @Protobuf
    private long userId;

    public LoginResultDTO() {
    }

    public LoginResultDTO(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }

}
