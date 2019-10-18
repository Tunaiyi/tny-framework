package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.MethodAccessor;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        return method;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return fastMethod.getParameterTypes();
    }

    @Override
    public Class<?> getReturnType() {
        return fastMethod.getReturnType();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return fastMethod.getDeclaringClass();
    }

    @Override
    public String getName() {
        return fastMethod.getName();
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return fastMethod.getExceptionTypes();
    }

    @Override
    public Object invoke(Object obj, Object... args) throws InvocationTargetException {
        return fastMethod.invoke(obj, args);
    }

}
