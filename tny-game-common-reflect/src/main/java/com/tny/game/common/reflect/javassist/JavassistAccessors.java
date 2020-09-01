package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.*;

import java.util.concurrent.*;

public class JavassistAccessors {

    private final static ConcurrentMap<Class<?>, ClassAccessor> classMap = new ConcurrentHashMap<>();

    public static ClassAccessor getGClass(Class<?> clazz) {
        ClassAccessor gClass = classMap.get(clazz);
        if (gClass != null) {
            return gClass;
        }
        synchronized (clazz) {
            gClass = classMap.get(clazz);
            if (gClass != null) {
                return gClass;
            }
            gClass = new JSsistClassAccessor(clazz, null);
            ClassAccessor oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

    public static ClassAccessor getGClass(Class<?> clazz, MethodFilter filter) {
        ClassAccessor gClass = classMap.get(clazz);
        if (gClass != null) {
            return gClass;
        }
        synchronized (clazz) {
            gClass = classMap.get(clazz);
            if (gClass != null) {
                return gClass;
            }
            gClass = new JSsistClassAccessor(clazz, filter);
            ClassAccessor oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

}
