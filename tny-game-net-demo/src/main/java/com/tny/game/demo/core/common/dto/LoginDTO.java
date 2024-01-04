/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.demo.core.common.dto;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

/**
 * <p>
 */

@DTODoc("登录DTO")
@ProtoEx(1000_01_00)
@ProtobufClass
@TypeProtobuf(1000_01_00)
public class LoginDTO {

    @ProtoExField(1)
    @Protobuf(order = 1)
    private long userId;

    @ProtoExField(2)
    @Protobuf(order = 2)
    private String message;

    @ProtoExField(3)
    @Protobuf(order = 3)
    private long certId;

    public LoginDTO() {
    }

    public LoginDTO(long certId, long userId, String message) {
        this.userId = userId;
        this.certId = certId;
        this.message = message;
    }

    public long getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }

    public long getCertId() {
        return this.certId;
    }

}
