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
