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
package com.tny.game.doc.holder;

import com.tny.game.common.utils.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Field;
import java.util.*;

public class EnumDocClass extends DocClass {

    private final List<DocField> enumList;

    private <E extends Enum<E>> EnumDocClass(Class<E> clazz) {
        super(clazz);
        this.enumList = Collections.unmodifiableList(createEnumFieldList(clazz));
    }

    public static <E extends Enum<E>> EnumDocClass createEnumClass(Class<E> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        Asserts.checkNotNull(classDoc, "{} is not classDoc", clazz);
        return new EnumDocClass(clazz);
    }

    private static <E extends Enum<E>> List<DocField> createEnumFieldList(Class<E> clazz) {
        List<DocField> list = new ArrayList<>();
        for (Enum<?> enumObject : EnumUtils.getEnumList(clazz)) {
            try {
                Field enumField = clazz.getDeclaredField(enumObject.name());
                DocField fieldDocHolder = DocField.create(clazz, enumField);
                if (fieldDocHolder != null) {
                    list.add(fieldDocHolder);
                }
            } catch (SecurityException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public List<DocField> getEnumList() {
        return this.enumList;
    }

}
