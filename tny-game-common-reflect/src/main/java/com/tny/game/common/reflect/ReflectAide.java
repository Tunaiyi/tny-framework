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

package com.tny.game.common.reflect;

import com.tny.game.common.reflect.exception.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class ReflectAide {

    /**
     * @author KGTny
     */
    public enum MethodType {

        /**
         *
         */
        GETTER("get", "is"),

        /**
         *
         */
        SETTER("set");

        public final String[] values;

        MethodType(String... values) {
            this.values = values;
        }

    }

    public static Set<Class<?>> getDeepInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaceClasses = new HashSet<>();
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            interfaceClasses.add(interfaceClass);
            interfaceClasses.addAll(getDeepInterfaces(interfaceClass));
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            interfaceClasses.addAll(getDeepInterfaces(superClass));
        }
        return interfaceClasses;
    }

    public static Set<Class<?>> getDeepClasses(Class<?> clazz) {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(clazz);
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            classes.add(interfaceClass);
            classes.addAll(getDeepInterfaces(interfaceClass));
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            classes.addAll(getDeepClasses(superClass));
        }
        return classes;
    }

    public static Field getDeepField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return getDeepField(superClass, name);
            }
        }
        return null;
    }

    @SafeVarargs
    public static List<Field> getDeepFieldsByAnnotation(Class<?> clazz, Class<? extends Annotation>... annotations) {
        List<Field> fieldList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            for (Class<? extends Annotation> annotation : annotations) {
                if (field.isAnnotationPresent(annotation)) {
                    field.setAccessible(true);
                    fieldList.add(field);
                    break;
                }
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            fieldList.addAll(getDeepFieldsByAnnotation(superClass, annotations));
        }
        return fieldList;
    }

    @SafeVarargs
    public static List<Method> getDeepMethodsByAnnotation(Class<?> clazz, Class<? extends Annotation>... annotations) {
        List<Method> methodList = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : annotations) {
                if (method.isAnnotationPresent(annotation)) {
                    method.setAccessible(true);
                    methodList.add(method);
                    break;
                }
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            methodList.addAll(getDeepMethodsByAnnotation(superClass, annotations));
        }
        return methodList;
    }

    public static List<Field> getDeepField(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            fieldList.add(field);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            fieldList.addAll(getDeepField(superClass));
        }
        return fieldList;
    }

    public static List<Method> getDeepMethod(Class<?> clazz) {
        List<Method> methodList = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            methodList.add(method);
        }
        for (Class<?> interClass : clazz.getInterfaces()) {
            methodList.addAll(getDeepMethod(interClass));
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            methodList.addAll(getDeepMethod(superClass));
        }
        return methodList;
    }

    public static Method getDeepMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Method method = findDeepMethod(clazz, name, paramTypes);
        if (method != null) {
            return method;
        }
        throw new MethodNotFoundException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", name: " + name
                                          + ", paramType: " + Arrays.toString(paramTypes) + "] exception");
    }

    private static Method findDeepMethod(Class<?> clazz, String name, Class<?>... paramType) {
        try {
            Method method = clazz.getDeclaredMethod(name, paramType);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return findDeepMethod(superClass, name, paramType);
            }
            return null;
        }
    }

    public static Method getPropertyMethod(Class<?> clazz, MethodType methodType, String name, Class<?>... paramTypes) {
        Method method;
        for (String value : methodType.values) {
            method = findDeepMethod(clazz, parseMethodName(value, name), paramTypes);
            if (method != null) {
                return method;
            }
        }
        throw new MethodNotFoundException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", name: " + name
                                          + ", paramType: " + Arrays.toString(paramTypes) + "] exception");
    }

    public static List<Method> getPropertyMethod(Class<?> clazz, MethodType methodType, String[] names,
            Class<?>[][] paramTypes) {
        List<Method> methodList = new ArrayList<>();
        for (int index = 0; index < names.length; index++) {
            String name = names[index];
            Class<?>[] params = null;
            if (paramTypes != null && index < paramTypes.length) {
                params = paramTypes[index];
            }
            Method method = getPropertyMethod(clazz, methodType, name, params);
            methodList.add(method);
        }
        return methodList;
    }

    protected static String parseMethodName(String head, String name) {
        return head + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static boolean isGetter(Method method) {
        // 原实现误调 checkSetter：getter 判 false、setter 判 true，语义完全颠倒
        return checkProperty(method) && checkGetter(method);
    }

    public static boolean isSetter(Method method) {
        return checkProperty(method) && checkSetter(method);
    }

    public static boolean isProperty(Method method) {
        return checkProperty(method) && (checkGetter(method) || checkSetter(method));
    }

    private static boolean checkGetter(Method method) {
        String methodName = method.getName();
        if (method.getParameterCount() != 0 || method.getReturnType() == void.class) {
            // getter 形态必须无参且非 void（原实现 getFoo(String) 也算 getter）
            return false;
        }
        if (methodName.startsWith("get")) {
            return true;
        }
        if (methodName.startsWith("is")) {
            Class<?> returnClazz = method.getReturnType();
            return returnClazz == boolean.class || returnClazz == Boolean.class;
        }
        return false;
    }

    private static boolean checkSetter(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("set")) {
            Class<?>[] paramClasses = method.getParameterTypes();
            return paramClasses.length == 1;
        }
        return false;
    }

    private static boolean checkProperty(Method method) {
        return !Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass() != Object.class;
    }

    public static List<Type> getFieldGenericTypes(Field field) {
        Type type = field.getGenericType();
        List<Type> types = new ArrayList<>();
        if (type instanceof Class) {
            return Collections.emptyList();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] genericTypes = paramType.getActualTypeArguments();
            Collections.addAll(types, genericTypes);
        }
        return types;
    }

    public static List<Class<?>> getFieldGenericClasses(Field field) {
        List<Type> genTypes = getFieldGenericTypes(field);
        List<Class<?>> classes = new ArrayList<>();
        for (Type type : genTypes) {
            if (type instanceof Class) {
                classes.add((Class<?>) type);
            } else if (type instanceof ParameterizedType) {
                classes.add((Class<?>) ((ParameterizedType) type).getRawType());
            }
        }
        return classes;
    }

    /**
     * @param clazz        泛型
     * @param genericClass 泛型接口
     * @return 返回泛型列表
     */
    public static List<Class<?>> getComponentType(Class<?> clazz, Class<?> genericClass) {
        return getComponentTypeInternal(clazz, genericClass,
                new java.util.LinkedHashSet<>(), new java.util.HashSet<>());
    }

    /**
     * 沿"直接泛型接口 + 父类链"逐层查找目标泛型声明（原只查直接父层：接口经抽象类中转时
     * 返回空，消费点静默跳过注册）。命中即收集其实际类型参数；visited 防环、结果按发现序去重。
     */
    private static List<Class<?>> getComponentTypeInternal(Class<?> clazz, Class<?> genericClass,
            Set<Class<?>> collected, Set<Class<?>> seen) {
        if (clazz == null || clazz == Object.class || !seen.add(clazz)) {
            return new ArrayList<>(collected);
        }
        collectTypeArgs(clazz.getGenericInterfaces(), genericClass, collected);
        collectTypeArgs(new Type[]{clazz.getGenericSuperclass()}, genericClass, collected);
        for (Type type : clazz.getGenericInterfaces()) {
            Class<?> raw = rawClass(type);
            if (raw != null) {
                getComponentTypeInternal(raw, genericClass, collected, seen);
            }
        }
        getComponentTypeInternal(clazz.getSuperclass(), genericClass, collected, seen);
        return new ArrayList<>(collected);
    }

    private static Class<?> rawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    private static void collectTypeArgs(Type[] types, Class<?> genericClass, Set<Class<?>> collected) {
        for (Type type : types) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType paramType = (ParameterizedType) type;
            if (paramType.getRawType() != genericClass) {
                continue;
            }
            for (Type t : paramType.getActualTypeArguments()) {
                Class<?> raw = rawClass(t);
                if (raw != null) {
                    collected.add(raw);
                }
            }
        }
    }

}
