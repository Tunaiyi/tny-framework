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

package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;

import java.util.concurrent.*;

public class CGlibUtils {

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
            gClass = new CGlibClassAccessor(clazz, null);
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
            gClass = new CGlibClassAccessor(clazz, filter);
            ClassAccessor oldClass = classMap.putIfAbsent(clazz, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

}
