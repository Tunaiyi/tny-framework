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

package com.tny.game.protoex.field;

import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;

import java.lang.reflect.*;

/**
 * 简单编码方式
 *
 * @param <T>
 * @author KGTny
 */
public class SimpleFieldOptions<T> extends BaseFieldOptions<T> {

    public SimpleFieldOptions(Class<T> type, int fieldIndex, ProtoExConf conf) {
        super(ProtoExType.getProtoExType(type), type, "element", fieldIndex, conf);
    }

    public SimpleFieldOptions(Class<T> type, int fieldIndex, TypeEncode typeEncode, FieldFormat format) {
        super(ProtoExType.getProtoExType(type), type, "element", fieldIndex, typeEncode, format);
    }

    public SimpleFieldOptions(EntryType entryType, Class<T> type, ProtoExConf conf) {
        this(entryType, type, conf.typeEncode(), conf.format());
    }

    public SimpleFieldOptions(EntryType entryType, Class<T> type, TypeEncode typeEncode, FieldFormat format) {
        super(ProtoExType.getProtoExType(type), type, entryType.name(), entryType.getFieldIndex(), typeEncode, format);
        this.packed = false;
    }

    private static class TC {

        int[] ints;

        Object[] objects;

        String[] strings;

        String type;

    }

    public static void main(String[] args) {
        for (Field field : TC.class.getDeclaredFields()) {
            // Arrays.
            Class<?> czz = field.getType().getComponentType();
            if (czz != null) {
                System.out.println(Array.newInstance(czz, 10));
            }
            System.out.println(field.getGenericType() instanceof GenericArrayType);
        }

    }

}
