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

package com.tny.game.common.type;

import com.tny.game.common.utils.*;

import java.lang.reflect.*;

/**
 * Created by Kun Yang on 2017/3/29.
 */
public abstract class ReferenceType<T> {

    private final Type type;

    protected ReferenceType() {
        Class<?> parameterizedTypeReferenceSubclass = findReferenceTypeSubclass(getClass());
        Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        Asserts.checkArgument(type instanceof ParameterizedType, "Type must be a parameterized type");
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Asserts.checkArgument(actualTypeArguments.length == 1, "Number of type arguments must be 1");
        this.type = actualTypeArguments[0];
    }

    private ReferenceType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof ReferenceType &&
                                  this.type.equals(((ReferenceType<?>) other).type)));
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public String toString() {
        return "ReferenceType<" + this.type + ">";
    }

    /**
     * Build a {@code ReferenceType} wrapping the given type.
     *
     * @param type a generic type (possibly obtained via reflection,
     *             e.g. from {@link java.lang.reflect.Method#getGenericReturnType()})
     * @return a corresponding reference which may be passed into
     * {@code ReferenceType}-accepting methods
     * @since 4.3.12
     */
    public static <T> ReferenceType<T> forType(Type type) {
        return new ReferenceType<T>(type) {

        };
    }

    private static Class<?> findReferenceTypeSubclass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (Object.class == parent) {
            throw new IllegalStateException("Expected ReferenceType superclass");
        } else if (ReferenceType.class == parent) {
            return child;
        } else {
            return findReferenceTypeSubclass(parent);
        }
    }

}
