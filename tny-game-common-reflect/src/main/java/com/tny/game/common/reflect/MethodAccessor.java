package com.tny.game.common.reflect;

import java.lang.reflect.*;

public interface MethodAccessor {

    /**
     * java原生Method对象
     *
     * @return
     */
    Method getJavaMethod();

    /**
     * 参数类型
     *
     * @return
     */
    Class<?>[] getParameterTypes();

    /**
     * 返回类型
     *
     * @return
     */
    Class<?> getReturnType();

    /**
     * 返回表示声明由此 Method 对象表示的方法的类或接口的 Class 对象。
     *
     * @return
     */
    Class<?> getDeclaringClass();

    /**
     * 方法名称
     *
     * @return
     */
    String getName();

    /**
     * 抛出异常类型
     *
     * @return
     */
    Class<?>[] getExceptionTypes();

    /**
     * 调用
     *
     * @param obj
     * @param args
     * @return
     * @throws InvocationTargetException
     */
    Object invoke(Object obj, Object... args) throws InvocationTargetException;

}
