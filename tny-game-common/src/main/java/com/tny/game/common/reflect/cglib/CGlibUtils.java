package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.ClassAccessor;
import com.tny.game.common.reflect.MethodFilter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CGlibUtils {

    private final static ConcurrentMap<Class<?>, ClassAccessor> classMap = new ConcurrentHashMap<Class<?>, ClassAccessor>();

    public static ClassAccessor getGClass(Class<?> clazz) {
        ClassAccessor gClass = classMap.get(clazz);
        if (gClass != null)
            return gClass;
        synchronized (clazz) {
            gClass = classMap.get(clazz);
            if (gClass != null)
                return gClass;
            gClass = new CGlibClassAccessor(clazz, null);
            ClassAccessor oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

    public static ClassAccessor getGClass(Class<?> clazz, MethodFilter filter) {
        ClassAccessor gClass = classMap.get(clazz);
        if (gClass != null)
            return gClass;
        synchronized (clazz) {
            gClass = classMap.get(clazz);
            if (gClass != null)
                return gClass;
            gClass = new CGlibClassAccessor(clazz, filter);
            ClassAccessor oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

}
