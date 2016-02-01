package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.GMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

public class JSsistMethod implements GMethod {

    private final Method method;

    private final MethodInvoker methodInvoker;

    public JSsistMethod(Method method) {
        this.method = method;
        this.methodInvoker = InvokerFactory.newInvoker(method);
    }

    public JSsistMethod(Method method, MethodInvoker methodInvoker) {
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
