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

package com.tny.game.common.enums;

import com.tny.game.common.utils.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Kun Yang on 16/2/3.
 */
public class EnumAide {

    private static final Map<Class<?>, Map<String, Object>> enumMap = new ConcurrentHashMap<>();

    public static <I, E extends Enumerable<I>> E check(Class<E> enumClass, I id) {
        return Asserts.checkNotNull(of(enumClass, id),
                "ID 为 {} 的 {} 枚举实例不存在", id, enumClass);
    }

    public static <E> E checkOfName(Class<E> enumClass, String name) {
        return Asserts.checkNotNull(ofName(enumClass, name),
                "ID 为 {} 的 {} 枚举实例不存在", name, enumClass);
    }

    public static <E extends Enum<E>, S> E check(Class<E> enumClass, Function<E, S> getter, S value) {
        return Asserts.checkNotNull(of(enumClass, getter, value),
                "{} 为 {} 的 {} 枚举实例不存在", getter, value, enumClass);
    }

    public static <E, S> Set<E> find(Class<E> enumClass, Function<E, S> getter, Collection<? extends S> value) {
        Set<E> enums = new HashSet<>();
        for (E e : enumClass.getEnumConstants()) {
            if (value.contains(getter.apply(e))) {
                enums.add(e);
            }
        }
        return enums;
    }

    public static <E, S> E of(Class<E> enumClass, Function<E, S> getter, S value) {
        for (E e : enumClass.getEnumConstants()) {
            if (Objects.equals(getter.apply(e), value)) {
                return e;
            }
        }
        return null;
    }

    public static <I, E extends Enumerable<I>> E of(Class<E> enumClass, I id) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    public static <E> E ofName(Class<E> enumClass, String enumName) {
        if (Enum.class.isAssignableFrom(enumClass)) {
            return ObjectAide.as(enumMap.computeIfAbsent(enumClass, c -> {
                try {
                    Method method = c.getMethod("values");
                    Object[] inter = (Object[]) method.invoke(null);
                    Map<String, Object> builder = new HashMap<>();
                    for (Object e : inter) {
                        // 以常量声明名为索引：自定义显示文本不得劫持名字查找（BREAKING：显示文本不再是合法名字）；
                        // 原 e.toString() 建键会使覆写显示文本的枚举按名查不中、同显示文本常量互相覆盖
                        builder.put(((Enum<?>) e).name(), e);
                    }
                    return Collections.unmodifiableMap(builder);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }).get(enumName));
        }
        return null;
    }

    @SafeVarargs
    public static <E extends Enum<E>> boolean isIn(E value, E... elements) {
        return Stream.of(elements).anyMatch(v -> v == value);
    }

    @SafeVarargs
    public static <E extends Enum<E>> boolean isOut(E value, E... elements) {
        return Stream.of(elements).noneMatch(v -> v == value);
    }

}
