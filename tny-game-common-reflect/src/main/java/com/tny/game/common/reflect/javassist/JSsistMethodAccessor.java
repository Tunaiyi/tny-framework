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
        } catch (Error e) {
            // Error 原样透传（与 cglib FastMethod 语义统一）：折叠成目标异常会吞掉 OOM/断言级信号
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e, MessageFormat.format("反射调用 {0} 异常", this.method));
        }
    }

}
