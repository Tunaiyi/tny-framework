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

package com.tny.game.protoex.field.runtime;

import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Enum类型描述结构
 *
 * @param <E>
 * @author KGTny
 */
public class EnumSchema<E extends Enum<E>> extends RuntimePrimitiveSchema<E> {

    private Class<E> typeClass;

    protected EnumSchema(Class<E> clazz) {
        super(0, clazz);
        this.typeClass = clazz;
        ProtoEx proto = this.typeClass.getAnnotation(ProtoEx.class);
        if (proto == null) {
            throw new RuntimeException(format("{} @{} is null", this.typeClass, ProtoEx.class));
        }
        this.protoExID = proto.value();
        this.raw = false;
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, E value, FieldOptions<?> options) {
        outputStream.writeEnum(value);
    }

    @Override
    public E readValue(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
        return inputStream.readEnum(this.typeClass);
    }

}
