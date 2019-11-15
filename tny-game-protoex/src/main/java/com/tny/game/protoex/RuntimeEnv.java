//========================================================================
//Copyright 2007-2011 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

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

        if (newInstanceFromObjectInputStream != null)
            newInstanceFromObjectInputStream.setAccessible(true);

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
