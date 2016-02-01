package com.tny.game.common.reflect;

public class Wraper {

    public static Class<?> getPrimitive(Class<?> wraperClass) {
        if (wraperClass.equals(Integer.class)) {
            return Integer.TYPE;
        }
        if (wraperClass.equals(Short.class)) {
            return Short.TYPE;
        }
        if (wraperClass.equals(Long.class)) {
            return Long.TYPE;
        }
        if (wraperClass.equals(Float.class)) {
            return Float.TYPE;
        }
        if (wraperClass.equals(Double.class)) {
            return Double.TYPE;
        }
        if (wraperClass.equals(Byte.class)) {
            return Byte.TYPE;
        }
        if (wraperClass.equals(Character.class)) {
            return Character.TYPE;
        }
        if (wraperClass.equals(Boolean.class)) {
            return Boolean.TYPE;
        }
        if (wraperClass.equals(Void.class)) {
            return Void.TYPE;
        }
        return wraperClass;
    }

    public static Class<?> getWraper(Class<?> toClass) {
        if (toClass.equals(Integer.TYPE)) {
            return Integer.class;
        }
        if (toClass.equals(Short.TYPE)) {
            return Short.class;
        }
        if (toClass.equals(Long.TYPE)) {
            return Long.class;
        }
        if (toClass.equals(Float.TYPE)) {
            return Float.class;
        }
        if (toClass.equals(Double.TYPE)) {
            return Double.class;
        }
        if (toClass.equals(Byte.TYPE)) {
            return Byte.class;
        }
        if (toClass.equals(Character.TYPE)) {
            return Character.class;
        }
        if (toClass.equals(Boolean.TYPE)) {
            return Boolean.class;
        }
        if (toClass.equals(Void.TYPE)) {
            return Void.class;
        }
        return toClass;
    }

    public static boolean isWraper(Class<?> toClass) {
        if (toClass.equals(Integer.class)) {
            return true;
        }
        if (toClass.equals(Short.class)) {
            return true;
        }
        if (toClass.equals(Long.class)) {
            return true;
        }
        if (toClass.equals(Float.class)) {
            return true;
        }
        if (toClass.equals(Double.class)) {
            return true;
        }
        if (toClass.equals(Byte.class)) {
            return true;
        }
        if (toClass.equals(Character.class)) {
            return true;
        }
        if (toClass.equals(Boolean.class)) {
            return true;
        }
        if (toClass.equals(Void.class)) {
            return true;
        }
        return false;
    }
}
