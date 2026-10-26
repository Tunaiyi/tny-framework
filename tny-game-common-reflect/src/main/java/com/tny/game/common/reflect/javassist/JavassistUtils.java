/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
