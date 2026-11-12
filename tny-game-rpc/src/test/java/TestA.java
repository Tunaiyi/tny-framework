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
