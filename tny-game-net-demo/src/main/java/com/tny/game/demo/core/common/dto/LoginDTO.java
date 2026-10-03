/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
