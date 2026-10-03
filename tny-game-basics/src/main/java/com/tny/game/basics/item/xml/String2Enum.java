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

package com.tny.game.basics.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.apache.commons.lang3.EnumUtils;

import java.text.MessageFormat;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * string转枚举
 *
 * @param <T>
 * @author KGTny
 */
public class String2Enum<T extends Enum<T>> extends AbstractSingleValueConverter {

    private List<Class<T>> enumClassList = new ArrayList<>();

    private HashMap<String, Enum<?>> enumMap = new HashMap<>();

    public String2Enum(Collection<Class<T>> enumClasses) {
        super();
        for (Class<T> clazz : enumClasses) {
            List<T> enums = EnumUtils.getEnumList(clazz);
            for (T e : enums) {
                Enum<?> oldEnum = this.enumMap.put(e.name(), e);
                if (oldEnum != null) {
                    throw new IllegalArgumentException(format("{}.{} 与 {}.{} name 相同!",
                            oldEnum.getClass(), oldEnum.name(),
                            e.getClass(), e.name()));
                }
            }
        }
        this.enumClassList.addAll(enumClasses);
    }

    @SuppressWarnings("unchecked")
    public String2Enum(Class<T>... enumClasses) {
        this(Arrays.asList(enumClasses));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean canConvert(Class clazz) {
        for (Class<T> enumClass : this.enumClassList) {
            if (clazz != Object.class && clazz.isAssignableFrom(enumClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object fromString(String value) {
        Object enumObject = this.enumMap.get(value);
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", value));
        }
        return enumObject;
    }

}
