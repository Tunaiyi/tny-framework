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
package com.tny.game.basics.item.xml.converter;

import com.tny.game.basics.converter.*;
import com.tny.game.expr.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class String2Enums<T extends Enum<T>> extends String2ExprHolderConverter {

    private final Class<? extends Collection> collectionClass;

    private final Map<String, Object> enumValueMap = new HashMap<>();

    @SafeVarargs
    public String2Enums(ExprHolderFactory exprHolderFactory, Class<? extends Collection> collectionClass, Class<T>... enumClasses) {
        super(exprHolderFactory);
        this.collectionClass = collectionClass;
        for (Class<T> enumClass : enumClasses) {
            for (Enum<?> enumValue : EnumSet.allOf(enumClass))
                this.enumValueMap.put(enumValue.name(), enumValue);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean canConvert(Class type) {
        return type.isAssignableFrom(this.collectionClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object fromString(String str) {
        Collection<?> collection;
        try {
            collection = this.collectionClass.getDeclaredConstructor().newInstance();
            collection.addAll(this.exprHolderFactory.create(str).createExpr().putAll(this.enumValueMap).execute(List.class));
            return collection;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }

}
