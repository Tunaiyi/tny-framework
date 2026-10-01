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
