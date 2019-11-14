package com.tny.game.common.enums;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.collection.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 16/2/3.
 */
public class EnumAide extends EnumUtils {

    private static final Map<Class<?>, Map<String, Object>> enumMap = new CopyOnWriteMap<>();

    public static <I, E extends EnumIdentifiable<I>> E of(Class<E> enumClass, I id) {
        return Throws.checkNotNull(uncheckOf(enumClass, id),
                "ID 为 {} 的 {} 枚举实例不存在", id, enumClass);
    }

    public static <E> E ofName(Class<E> enumClass, String name) {
        return Throws.checkNotNull(uncheckOfName(enumClass, name),
                "ID 为 {} 的 {} 枚举实例不存在", name, enumClass);
    }

    public static <E extends Enum<E>, S> E of(Class<E> enumClass, Function<E, S> getter, S value) {
        return Throws.checkNotNull(uncheckOf(enumClass, getter, value),
                "{} 为 {} 的 {} 枚举实例不存在", getter, value, enumClass);
    }

    public static <E, S> Set<E> find(Class<E> enumClass, Function<E, S> getter, Collection<? extends S> value) {
        Set<E> enums = new HashSet<>();
        for (E e : enumClass.getEnumConstants()) {
            if (value.contains(getter.apply(e)))
                enums.add(e);
        }
        return enums;
    }

    public static <E, S> E uncheckOf(Class<E> enumClass, Function<E, S> getter, S value) {
        for (E e : enumClass.getEnumConstants()) {
            if (Objects.equals(getter.apply(e), value))
                return e;
        }
        return null;
    }

    public static <I, E extends EnumIdentifiable<I>> E uncheckOf(Class<E> enumClass, I id) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getId().equals(id))
                return e;
        }
        return null;
    }

    public static <E> E uncheckOfName(Class<E> enumClass, String enumName) {
        if (Enum.class.isAssignableFrom(enumClass)) {
            return as(enumMap.computeIfAbsent(enumClass, c -> {
                try {
                    Method method = c.getMethod("values");
                    Object[] inter = (Object[]) method.invoke(null);
                    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
                    for (Object e : inter) {
                        builder.put(e.toString(), e);
                    }
                    return builder.build();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }).get(enumName));
        }
        return null;
    }

    public static <E extends Enum<E>> boolean isIn(E value, E... elements) {
        return Stream.of(elements).anyMatch(v -> v == value);
    }

    public static <E extends Enum<E>> boolean isOut(E value, E... elements) {
        return Stream.of(elements).noneMatch(v -> v == value);
    }

}
