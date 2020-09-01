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
    public static List<Field> getDeepFieldByAnnotation(Class<?> clazz, Class<? extends Annotation>... annotations) {
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
            fieldList.addAll(getDeepFieldByAnnotation(superClass, annotations));
        }
        return fieldList;
    }

    @SafeVarargs
    public static List<Method> getDeepMethodByAnnotation(Class<?> clazz, Class<? extends Annotation>... annotations) {
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
            methodList.addAll(getDeepMethodByAnnotation(superClass, annotations));
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

    public static Method getDeepMethod(Class<?> clazz, String name, Class<?>... paramType) {
        try {
            Method method = clazz.getDeclaredMethod(name, paramType);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return getDeepMethod(superClass, name, paramType);
            }
            throw new MethodNotFoundException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", name: " + name
                    + ", paramType: " + Arrays.toString(paramType) + "] exception", e);
        }
    }

    public static Method getPropertyMethod(Class<?> clazz, MethodType methodType, String name, Class<?>... paramType) {
        Method method;
        for (String value : methodType.values) {
            try {
                method = clazz.getDeclaredMethod(parseMethodName(value, name), paramType);
                method.setAccessible(true);
                Class<?> returnClazz = method.getReturnType();
                if (value.equals("is") && (returnClazz == boolean.class || returnClazz == Boolean.class)) {
                    return method;
                }
            } catch (Exception e) {
                throw new ReflectException(e);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            try {
                return getPropertyMethod(superClass, methodType, name, paramType);
            } catch (Exception e) {
                throw new MethodNotFoundException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", MethodType: "
                        + methodType + ", name: " + name + ", paramType: " + Arrays.toString(paramType) + "] exception", e);
            }
        } else {
            throw new MethodNotFoundException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", MethodType: "
                    + methodType + ", name: " + name + ", paramType: " + Arrays.toString(paramType) + "] exception");
        }
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
        return checkProperty(method) && checkSetter(method);
    }

    public static boolean isSetter(Method method) {
        return checkProperty(method) && checkSetter(method);
    }

    public static boolean isProperty(Method method) {
        return checkProperty(method) && (checkGetter(method) || checkSetter(method));
    }

    private static boolean checkGetter(Method method) {
        Class<?> returnClazz = method.getReturnType();
        String methodName = method.getName();
        return methodName.startsWith("get") || methodName.startsWith("is") && (returnClazz == boolean.class || returnClazz == Boolean.class);
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
            ParameterizedType paramType = (ParameterizedType)type;
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
                classes.add((Class<?>)type);
            } else if (type instanceof ParameterizedType) {
                classes.add((Class<?>)((ParameterizedType)type).getRawType());
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
        List<Class<?>> classes = new ArrayList<>();
        if (genericClass.isInterface()) {
            for (Type type : clazz.getGenericInterfaces()) {
                if (!(type instanceof ParameterizedType)) {
                    continue;
                }
                ParameterizedType paramType = (ParameterizedType)type;
                if (paramType.getRawType() != genericClass) {
                    continue;
                }
                for (Type t : paramType.getActualTypeArguments()) {
                    if (t instanceof Class) {
                        classes.add((Class<?>)t);
                    } else if (t instanceof ParameterizedType) {
                        classes.add((Class<?>)((ParameterizedType)t).getRawType());
                    }
                }
            }
        } else {
            Type type = clazz.getGenericSuperclass();
            if (!(type instanceof ParameterizedType)) {
                return classes;
            }
            ParameterizedType paramType = (ParameterizedType)type;
            if (paramType.getRawType() != genericClass) {
                return classes;
            }
            for (Type t : paramType.getActualTypeArguments()) {
                if (t instanceof Class) {
                    classes.add((Class<?>)t);
                } else if (t instanceof ParameterizedType) {
                    classes.add((Class<?>)((ParameterizedType)t).getRawType());
                }
            }
        }
        return classes;
    }

}
