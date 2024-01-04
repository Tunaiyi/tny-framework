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
