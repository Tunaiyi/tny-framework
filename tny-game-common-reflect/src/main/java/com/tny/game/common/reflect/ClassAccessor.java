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

package com.tny.game.common.reflect;

import java.lang.reflect.*;
import java.util.*;

public interface ClassAccessor {

    /**
     * 获取java原生class
     *
     * @return
     */
    Class<?> getJavaClass();

    /**
     * 获取类名称
     *
     * @return
     */
    String getName();

    /**
     * 创建新实例
     *
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    Object newInstance() throws InvocationTargetException, InstantiationException;

    /**
     * 获取方法列表
     *
     * @return
     */
    List<MethodAccessor> getGMethodList();

    /**
     * 通过指定方法获取方法
     *
     * @param method
     * @return
     */
    MethodAccessor getMethod(Method method);

    /**
     * 通过名字和参数获取方法
     *
     * @param name
     * @param parameterTypes
     * @return
     */
    MethodAccessor getMethod(String name, Class<?>... parameterTypes);

    Map<String, PropertyAccessor> getAccessorMap();

    PropertyAccessor getProperty(String name);

    PropertyAccessor getProperty(String name, Class<?> clazz);

}
