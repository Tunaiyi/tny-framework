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
package com.tny.game.doc;

import com.tny.game.common.enums.*;
import com.tny.game.common.utils.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * 语言转换器
 * Created by Kun Yang on 16/1/31.
 */
public enum LangTypeFormatter implements TypeFormatter {

    RAW {
        @Override
        public String format(Type type) {
            if (type instanceof Class) {
                return ((Class<?>)type).getSimpleName();
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                Class<?> javaType = (Class<?>)pType.getRawType();
                return javaType.getSimpleName();
            }
            throw new IllegalArgumentException(StringAide.format("{} 类型 无法进行map", type));
        }
    },

    AS3 {
        final Set<Class<?>> intClassSet = new HashSet<>(Arrays.asList(new Class<?>[]{
                Integer.class, int.class, Short.class, short.class, Byte.class, byte.class
        }));
        final Set<Class<?>> numberClassSet = new HashSet<>(Arrays.asList(new Class<?>[]{
                Long.class, long.class, Float.class, float.class, Double.class, double.class
        }));

        @Override
        public String format(Type type) {
            if (type instanceof Class) {
                Class<?> javaType = (Class<?>)type;
                if (javaType.isEnum()) {
                    return "String";
                } else if (intClassSet.contains(javaType)) {
                    return "int";
                } else if (javaType == Boolean.class || javaType == boolean.class) {
                    return "Boolean";
                } else if (numberClassSet.contains(javaType)) {
                    return "Number";
                } else if (javaType == byte[].class || javaType == Byte[].class) {
                    return "ByteArray";
                }
                return javaType.getSimpleName();
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                Class<?> javaType = (Class<?>)pType.getRawType();
                if (Collection.class.isAssignableFrom(javaType)) {
                    return "Array";
                } else if (Map.class.isAssignableFrom(javaType)) {
                    return "Object";
                }
            }
            throw new IllegalArgumentException(StringAide.format("{} 类型 无法进行map", type));
        }
    },

    CSHARP {
        @Override
        public String format(Type type) {
            return LangTypeFormatter.cshapFormat(type, true);
        }
    },

    CSHARP_ENUM {
        @Override
        public String format(Type type) {
            return LangTypeFormatter.cshapFormat(type, false);
        }
    }
    //
    ;

    private static String cshapFormat(Type type, boolean formatEnum) {
        if (type instanceof Class) {
            Class<?> javaType = (Class<?>)type;
            if (Enumerable.class.isAssignableFrom(javaType)) {
                return "int";
            } else if (formatEnum && (javaType.isEnum() || String.class.isAssignableFrom(javaType))) {
                return "string";
            } else if (javaType == Long.class || javaType == long.class) {
                return "long";
            } else if (javaType == Integer.class || javaType == int.class) {
                return "int";
            } else if (javaType == Short.class || javaType == short.class) {
                return "short";
            } else if (javaType == Byte.class || javaType == byte.class) {
                return "sbyte";
            } else if (javaType == Double.class || javaType == double.class) {
                return "double";
            } else if (javaType == Float.class || javaType == float.class) {
                return "float";
            } else if (javaType == Boolean.class || javaType == boolean.class) {
                return "bool";
            } else if (javaType == byte[].class || javaType == Byte[].class) {
                return "byte []";
            }
            return javaType.getSimpleName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            Class<?> javaType = (Class<?>)pType.getRawType();
            if (Collection.class.isAssignableFrom(javaType)) {
                return "List<" + cshapFormat(pType.getActualTypeArguments()[0], formatEnum) + ">";
            } else if (Map.class.isAssignableFrom(javaType)) {
                return "Dictionary<" +
                        cshapFormat(pType.getActualTypeArguments()[0], formatEnum) + ", " +
                        cshapFormat(pType.getActualTypeArguments()[0], formatEnum) + ">";
            }
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> pType = (TypeVariable<?>)type;
            var gType = pType.getGenericDeclaration();
            if (gType instanceof Type) {
                return cshapFormat((Type)gType, formatEnum);
            }
        }
        throw new IllegalArgumentException(StringAide.format("{} 类型 无法进行map", type));
    }
    //    private Function<Type, String> fn;
    //
    //    LangTypeFormatter(Function<Type, String> fn) {
    //        this.fn = fn;
    //    }
    //
    //    @Override
    //    public String format(Type type) {
    //        return fn.apply(type);
    //    }
}
