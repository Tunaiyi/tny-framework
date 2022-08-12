/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
