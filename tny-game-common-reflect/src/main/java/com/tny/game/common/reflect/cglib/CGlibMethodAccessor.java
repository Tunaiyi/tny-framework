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

package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.*;

public class CGlibMethodAccessor implements MethodAccessor {

    private final Method method;

    private final FastMethod fastMethod;

    public CGlibMethodAccessor(Method method, FastMethod fastMethod) {
        super();
        this.method = method;
        this.fastMethod = fastMethod;
    }

    @Override
    public Method getJavaMethod() {
        return this.method;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.fastMethod.getParameterTypes();
    }

    @Override
    public Class<?> getReturnType() {
        return this.fastMethod.getReturnType();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.fastMethod.getDeclaringClass();
    }

    @Override
    public String getName() {
        return this.fastMethod.getName();
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return this.fastMethod.getExceptionTypes();
    }

    @Override
    public Object invoke(Object obj, Object... args) throws InvocationTargetException {
        try {
            return this.fastMethod.invoke(obj, args);
        } catch (InvocationTargetException e) {
            // 与 javassist 面统一：cglib FastMethod 把目标 Error 一并折叠，须在边界解包原样透传；
            // 其余异常保持折叠包装（与 javassist 面包装口径一致）
            Throwable target = e.getTargetException();
            if (target instanceof Error) {
                throw (Error) target;
            }
            throw e;
        }
    }

}
