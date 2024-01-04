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

package com.tny.game.data.storage;

import java.util.*;

/**
 * <p>
 */
public interface ObjectStorage<K extends Comparable<?>, O> {

    String getDataSource();

    O get(K id);

    List<O> get(List<K> id);

    boolean insert(K id, O object);

    boolean update(K id, O object);

    boolean save(K id, O object);

    boolean delete(K key, O object);

    /**
     * 按索引字段查找
     *
     * @param query       索引调节
     * @param returnClass 返回类型
     * @return 返回查找信息
     */
    <T> List<T> find(Map<String, Object> query, Class<T> returnClass);

    /**
     * 查找所有
     *
     * @param returnClass 返回类型
     * @return 返回查找信息
     */
    <T> List<T> findAll(Class<T> returnClass);

    /**
     * 按索引字段查找实体类
     *
     * @param findValue 索引调节
     * @return 返回查找信息
     */
    List<O> find(Map<String, Object> findValue);

    /**
     * 查找所有实体类
     *
     * @return 返回查找信息
     */
    List<O> findAll();

}
