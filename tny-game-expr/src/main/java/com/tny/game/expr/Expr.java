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

package com.tny.game.expr;

import java.util.Map;

public interface Expr {

    /**
     * 设置属性 <br>
     *
     * @param key   键
     * @param value 值
     * @return 表达式本身
     */
    Expr put(final String key, final Object value);

    /**
     * 设置属性Map <br>
     *
     * @param attribute 属性Map
     * @return 表达式本身
     */
    Expr putAll(final Map<String, Object> attribute);

    /**
     * 清除所有属性 <br>
     *
     * @return 表达式本身
     */
    Expr clear();

    /**
     * 移除指定键对应的属性 <br>
     *
     * @param key 指定键
     * @return 表达式本身
     */
    Expr remove(final String key);

    /**
     * 执行表达式计算,返回结果 <br>
     *
     * @param clazz 返回类型
     * @return 返回结果
     */
    <T> T execute(final Class<T> clazz);

}
