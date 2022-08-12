/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.*;

import java.lang.reflect.*;
import java.text.MessageFormat;

public class JSsistMethodAccessor implements MethodAccessor {

    private final Method method;

    private final MethodInvoker methodInvoker;

    public JSsistMethodAccessor(Method method) {
        this.method = method;
        this.methodInvoker = InvokerFactory.newInvoker(method);
    }

    public JSsistMethodAccessor(Method method, MethodInvoker methodInvoker) {
        super();
        this.method = method;
        this.methodInvoker = methodInvoker;
    }

    @Override
    public Method getJavaMethod() {
        return this.method;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.method.getParameterTypes();
    }

    @Override
    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.method.getDeclaringClass();
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return this.method.getExceptionTypes();
    }

    @Override
    public Object invoke(Object obj, Object... args) throws InvocationTargetException {
        try {
            return this.methodInvoker.invoke(obj, args);
        } catch (Throwable e) {
            throw new InvocationTargetException(e, MessageFormat.format("反射调用 {0} 异常", this.method));
        }
    }

}
