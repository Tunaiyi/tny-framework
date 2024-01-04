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
