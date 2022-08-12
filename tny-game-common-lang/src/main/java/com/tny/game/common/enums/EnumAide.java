/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
                    Object[] inter = (Object[])method.invoke(null);
                    Map<String, Object> builder = new HashMap<>();
                    for (Object e : inter) {
                        builder.put(e.toString(), e);
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
