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

package com.tny.game.common.reflect.javassist;

import javassist.*;

import java.lang.reflect.Method;
import java.util.*;

public class JavassistUtils {

    public static CtMethod getMethodBy(Class<?> clazz, Method method) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        return getCtMethod(clazz, method, pool);
    }

    public static CtMethod getMethodBy(ClassPool pool, Class<?> clazz, Method method) throws NotFoundException {
        return getCtMethod(clazz, method, pool);
    }

    public static CtMethod getMethodBy(ClassPool pool, CtClass ctClass, Method method) throws NotFoundException {
        return getCtMethod(pool, ctClass, method);
    }

    private static CtMethod getCtMethod(Class<?> clazz, Method method, ClassPool pool) throws NotFoundException {
        CtClass ctClass = pool.getCtClass(clazz.getName());
        return getCtMethod(pool, ctClass, method);
    }

    private static CtMethod getCtMethod(ClassPool pool, CtClass ctClass, Method method) throws NotFoundException {
        List<CtClass> paramCCs = new ArrayList<>();
        for (Class<?> paramClass : method.getParameterTypes()) {
            paramCCs.add(pool.get(paramClass.getCanonicalName()));
        }
        return ctClass.getDeclaredMethod(method.getName(), paramCCs.toArray(new CtClass[0]));
    }

}
