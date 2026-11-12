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
