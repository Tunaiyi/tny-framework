package com.tny.game.common.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface GClass {

    /**
     * 获取java原生class
     *
     * @return
     */
    public Class<?> getJavaClass();

    /**
     * 获取类名称
     *
     * @return
     */
    public String getName();

    /**
     * 创建新实例
     *
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public Object newInstance() throws InvocationTargetException, InstantiationException;

    /**
     * 获取方法列表
     *
     * @return
     */
    public List<GMethod> getGMethodList();

    /**
     * 通过指定方法获取方法
     *
     * @param method
     * @return
     */
    public GMethod getMethod(Method method);

    /**
     * 通过名字和参数获取方法
     *
     * @param name
     * @param parameterTypes
     * @return
     */
    public GMethod getMethod(String name, Class<?>... parameterTypes);

    public Map<String, GPropertyAccessor> getAccessorMap();

    public GPropertyAccessor getProperty(String name);

    public GPropertyAccessor getProperty(String name, Class<?> clazz);

}
