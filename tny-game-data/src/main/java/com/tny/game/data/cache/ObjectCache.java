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
