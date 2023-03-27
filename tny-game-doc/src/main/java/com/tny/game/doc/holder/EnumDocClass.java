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
package com.tny.game.doc.holder;

import com.tny.game.common.utils.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
