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
        return this.fastMethod.invoke(obj, args);
    }

}
