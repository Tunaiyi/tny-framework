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

package com.tny.game.protoex;

import java.lang.reflect.*;
import java.util.Properties;

/**
 * The runtime environment.
 *
 * @author David Yu
 * @created Jul 8, 2011
 */
public final class RuntimeEnv {

    public static final boolean ENUMS_BY_NAME;

    static final Method newInstanceFromObjectInputStream;

    static final Constructor<Object> OBJECT_CONSTRUCTOR;

    static {
        Constructor<Object> c = null;
        Class<?> reflectionFactoryClass = null;
        try {
            c = Object.class.getConstructor((Class[]) null);
            reflectionFactoryClass = Thread.currentThread().getContextClassLoader().loadClass("sun.reflect.ReflectionFactory");
        } catch (Exception e) {
            // ignore
        }

        OBJECT_CONSTRUCTOR = c != null && reflectionFactoryClass != null ? c : null;

        newInstanceFromObjectInputStream = OBJECT_CONSTRUCTOR == null ? getMethodNewInstanceFromObjectInputStream() : null;

        if (newInstanceFromObjectInputStream != null) {
            newInstanceFromObjectInputStream.setAccessible(true);
        }

        Properties props = OBJECT_CONSTRUCTOR == null ? new Properties() : System.getProperties();

        ENUMS_BY_NAME = Boolean.parseBoolean(props.getProperty("protostuff.runtime.enums_by_name", "false"));

    }

    private static Method getMethodNewInstanceFromObjectInputStream() {
        try {
            return java.io.ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> loadClass(String className) {
        try {
            return (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private RuntimeEnv() {
    }

    public static abstract class Instantiator<T> {

        /**
         * Creates a new instance of an object.
         */
        public abstract T newInstance();

    }

    static final class DefaultInstantiator<T> extends Instantiator<T> {

        final Constructor<T> constructor;

        DefaultInstantiator(Constructor<T> constructor) {
            this.constructor = constructor;
            constructor.setAccessible(true);
        }

        public T newInstance() {
            try {
                return constructor.newInstance((Object[]) null);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

    static final class Android2Instantiator<T> extends Instantiator<T> {

        final Class<T> clazz;

        Android2Instantiator(Class<T> clazz) {
            this.clazz = clazz;
        }

        @SuppressWarnings("unchecked")
        public T newInstance() {
            try {
                return (T) newInstanceFromObjectInputStream.invoke(null, clazz, Object.class);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
