/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
