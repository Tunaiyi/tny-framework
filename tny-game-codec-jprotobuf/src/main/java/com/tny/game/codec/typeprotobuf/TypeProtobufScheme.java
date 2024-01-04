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

package com.tny.game.codec.typeprotobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.tny.game.codec.protobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.common.utils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 12:49 下午
 */
public class TypeProtobufScheme<T> {

    private final int id;

    private final Class<T> type;

    private final Codec<T> codec;

    TypeProtobufScheme(Class<T> type) {
        this.type = type;
        TypeProtobuf typeProtobuf = this.type.getAnnotation(TypeProtobuf.class);
        Asserts.checkNotNull(typeProtobuf, "{} class annotation {} no exist",
                type, TypeProtobuf.class);
        this.id = typeProtobuf.value();
        this.codec = ProtobufCodecManager.getInstance().loadCodec(type);
    }

    public int getId() {
        return this.id;
    }

    public Class<T> getType() {
        return this.type;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }

}
