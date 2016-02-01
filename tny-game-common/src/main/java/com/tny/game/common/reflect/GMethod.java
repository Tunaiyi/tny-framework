package com.tny.game.common.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface GMethod {

    /**
     * java原生Method对象
     *
     * @return
     */
    public Method getJavaMethod();

    /**
     * 参数类型
     *
     * @return
     */
    public Class<?>[] getParameterTypes();

    /**
     * 返回类型
     *
     * @return
     */
    public Class<?> getReturnType();

    /**
     * 返回表示声明由此 Method 对象表示的方法的类或接口的 Class 对象。
     *
     * @return
     */
    public Class<?> getDeclaringClass();

    /**
     * 方法名称
     *
     * @return
     */
    public String getName();

    /**
     * 抛出异常类型
     *
     * @return
     */
    public Class<?>[] getExceptionTypes();

    /**
     * 调用
     *
     * @param obj
     * @param args
     * @return
     * @throws InvocationTargetException
     */
    public Object invoke(Object obj, Object... args) throws InvocationTargetException;

}
