package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.GClass;
import com.tny.game.common.reflect.MethodFilter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CGlibUtils {

    private final static ConcurrentMap<Class<?>, GClass> classMap = new ConcurrentHashMap<Class<?>, GClass>();

    public static GClass getGClass(Class<?> clazz) {
        GClass gClass = classMap.get(clazz);
        if (gClass != null)
            return gClass;
        synchronized (clazz) {
            gClass = classMap.get(clazz);
            if (gClass != null)
                return gClass;
            gClass = new CGlibClass(clazz, null);
            GClass oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

    public static GClass getGClass(Class<?> clazz, MethodFilter filter) {
        GClass gClass = classMap.get(clazz);
        if (gClass != null)
            return gClass;
        synchronized (clazz) {
            gClass = classMap.get(clazz);
            if (gClass != null)
                return gClass;
            gClass = new CGlibClass(clazz, filter);
            GClass oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

}
