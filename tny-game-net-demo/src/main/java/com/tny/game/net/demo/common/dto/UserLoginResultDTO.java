package com.tny.game.net.demo.common.dto;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.message.codec.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 2:49 下午
 */
@ProtobufClass
@ProtobufType(10_00_0000)
public class UserLoginResultDTO {

    @Protobuf
    private long userId;

    public UserLoginResultDTO() {
    }

    public UserLoginResultDTO(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }

}
