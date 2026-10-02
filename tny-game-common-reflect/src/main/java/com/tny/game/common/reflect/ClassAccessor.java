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
