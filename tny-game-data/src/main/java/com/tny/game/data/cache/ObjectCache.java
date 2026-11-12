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

package com.tny.game.data.cache;

/**
 * 获取
 * <p>
 */
public interface ObjectCache<K extends Comparable<?>, O> {

    /**
     * @return 缓存
     */
    EntityScheme getScheme();

    /**
     * 通过 key 获取 对象
     *
     * @param key 键值
     * @return 返回
     */
    O get(K key);

    /**
     * 放入缓存
     *
     * @param key    键值
     * @param object 对象
     */
    void put(K key, O object);

    /**
     * 移除指定 key 值
     *
     * @param key    键值
     * @param object 值
     */
    boolean remove(K key, O object);

    /**
     * @return 数量
     */
    int size();

}
