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
