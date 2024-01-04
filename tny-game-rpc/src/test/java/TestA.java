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

import java.lang.reflect.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:27 下午
 */
public class TestA {

    private static Class<?> bodyGenericType(Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type[] actualTypeValue = ((ParameterizedType) type).getActualTypeArguments();
            Type typeClass = actualTypeValue[0];
            if (typeClass instanceof ParameterizedType) {
                ParameterizedType bodyType = (ParameterizedType) typeClass;
                return (Class<?>) bodyType.getRawType();
            }
            return (Class<?>) typeClass;
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) {

        for (Method method : TInterface.class.getMethods()) {
            System.out.println(bodyGenericType(method));
        }
    }

}
