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

package com.tny.game.basics.item.loader.jackson;

import org.apache.commons.lang3.EnumUtils;

import java.text.MessageFormat;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumMapper<T> {

    private final Class<T> parent;

    private final HashMap<String, Enum<?>> enumMap = new HashMap<>();

    public EnumMapper(Class<T> parent, Class<?> enumClass) {
        this(parent, Collections.singletonList(enumClass));
    }

    public EnumMapper(Class<T> parent, Collection<? extends Class<?>> enumClasses) {
        this.parent = parent;
        for (Class<?> clazz : enumClasses) {
            if (!clazz.isEnum()) {
                throw new ClassCastException(format("{} 不属于 Enum", clazz));
            }
            if (!this.parent.isAssignableFrom(clazz)) {
                throw new ClassCastException(format("{} 不属于 {} 子类", clazz, this.parent));
            }
            List<Enum> enums = EnumUtils.getEnumList(as(clazz));
            for (Enum e : enums) {
                Enum<?> oldEnum = this.enumMap.put(e.name(), e);
                if (oldEnum != null) {
                    throw new IllegalArgumentException(format("{}.{} 与 {}.{} name 相同!",
                            oldEnum.getClass(), oldEnum.name(),
                            e.getClass(), e.name()));
                }
            }
        }
    }

    public T getEnum(String value) {
        T enumObject = as(this.enumMap.get(value));
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", value));
        }
        return enumObject;
    }

}
