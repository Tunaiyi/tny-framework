package com.tny.game.common.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtils {

    /**
     * @author KGTny
     */
    public enum MethodType {

        /**
         * @uml.property name="getter"
         * @uml.associationEnd
         */
        Getter("get", "is"),

        /**
         * @uml.property name="setter"
         * @uml.associationEnd
         */
        Setter("set");

        public final String[] values;

        private MethodType(String... values) {
            this.values = values;
        }

    }

    public static List<Class<?>> getDeepInterface(Class<?> clazz) {
        List<Class<?>> interfaceClassList = new ArrayList<Class<?>>();
        for (Class<?> interfacer : clazz.getInterfaces()) {
            interfaceClassList.add(interfacer);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class)
            interfaceClassList.addAll(getDeepInterface(superClass));
        return interfaceClassList;
    }

    public static Field getDeepField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            return field;
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
        List<Field> fieldList = new ArrayList<Field>();
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
        if (superClass != null && superClass != Object.class)
            fieldList.addAll(getDeepFieldByAnnotation(superClass, annotations));
        return fieldList;
    }

    @SafeVarargs
    public static List<Method> getDeepMethodByAnnotation(Class<?> clazz, Class<? extends Annotation>... annotations) {
        List<Method> methodList = new ArrayList<Method>();
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
        if (superClass != null && superClass != Object.class)
            methodList.addAll(getDeepMethodByAnnotation(superClass, annotations));
        return methodList;
    }

    public static List<Field> getDeepField(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            fieldList.add(field);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class)
            fieldList.addAll(getDeepField(superClass));
        return fieldList;
    }

    public static List<Method> getDeepMethod(Class<?> clazz) {
        List<Method> methodList = new ArrayList<Method>();
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            methodList.add(method);
        }
        for (Class<?> interClass : clazz.getInterfaces()) {
            methodList.addAll(getDeepMethod(interClass));
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class)
            methodList.addAll(getDeepMethod(superClass));
        return methodList;
    }

    public static Method getDeepMethod(Class<?> clazz, String name, Class<?>... paramType) {
        try {
            Method method = clazz.getDeclaredMethod(name, paramType);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class)
                return getDeepMethod(superClass, name, paramType);
            throw new NoSuchMethodException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", name: " + name
                    + ", paramType: " + Arrays.toString(paramType) + "] exception", e);
        }
    }

    public static Method getPropertyMethod(Class<?> clazz, MethodType methodType, String name, Class<?>... paramType) {
        Method method = null;
        for (String value : methodType.values) {
            if (method == null) {
                try {
                    method = clazz.getDeclaredMethod(parseMethodName(value, name), paramType);
                    if (method != null) {
                        method.setAccessible(true);
                        Class<?> returnClazz = method.getReturnType();
                        if (value.equals("is") && returnClazz != boolean.class && returnClazz == Boolean.class)
                            method = null;
                        else
                            return method;
                    }
                } catch (Exception e) {
                }
            }
        }
        if (method == null) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                try {
                    return getPropertyMethod(superClass, methodType, name, paramType);
                } catch (Exception e) {
                    throw new NoSuchMethodException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", MethodType: "
                            + methodType + ", name: " + name + ", paramType: " + Arrays.toString(paramType) + "] exception", e);
                }
            } else
                throw new NoSuchMethodException("ReflectUtils.getPropertyMethod [clazz: " + clazz + ", MethodType: "
                        + methodType + ", name: " + name + ", paramType: " + Arrays.toString(paramType) + "] exception");
        }
        return method;
    }

    public static List<Method> getPropertyMethod(Class<?> clazz, MethodType methodType, String[] names,
                                                 Class<?>[][] paramTypes) {
        List<Method> methodList = new ArrayList<Method>();
        for (int index = 0; index < names.length; index++) {
            String name = names[index];
            Class<?>[] params = null;
            if (paramTypes != null && index < paramTypes.length)
                params = paramTypes[index];
            Method method = getPropertyMethod(clazz, methodType, name, params);
            if (method != null)
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
        return (methodName.startsWith("get") && returnClazz != null)
                || (methodName.startsWith("is")
                && (returnClazz == boolean.class || returnClazz == Boolean.class));
    }

    private static boolean checkSetter(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("set")) {
            Class<?>[] paramClasses = method.getParameterTypes();
            return paramClasses != null && paramClasses.length == 1;
        }
        return false;
    }

    private static boolean checkProperty(Method method) {
        if (Modifier.isStatic(method.getModifiers()) || method.getDeclaringClass() == Object.class)
            return false;
        return true;
    }

    public static List<Class<?>> getComponentType(Class<?> clazz, Class<?> genericClass) {
        List<Class<?>> classes = new ArrayList<>();
        if (genericClass.isInterface()) {
            for (Type type : clazz.getGenericInterfaces()) {
                if (!(type instanceof ParameterizedType))
                    continue;
                ParameterizedType paramType = (ParameterizedType) type;
                if (paramType.getRawType() != genericClass)
                    continue;
                for (Type t : paramType.getActualTypeArguments())
                    classes.add((Class<?>) t);
            }
        } else {
            Type type = clazz.getGenericSuperclass();
            if (!(type instanceof ParameterizedType))
                return classes;
            ParameterizedType paramType = (ParameterizedType) type;
            if (paramType.getRawType() != genericClass)
                return classes;
            for (Type t : paramType.getActualTypeArguments())
                classes.add((Class<?>) t);
        }
        return classes;
    }

}
