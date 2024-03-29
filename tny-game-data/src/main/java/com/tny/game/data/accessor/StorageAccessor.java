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
package com.tny.game.data.accessor;

import java.util.*;

/**
 * @author KGTny
 */
public interface StorageAccessor<K extends Comparable<?>, O> {

    /**
     * 按索引字段查找
     *
     * @param findValue   索引调节
     * @param returnClass 返回类型
     * @return 返回查找信息
     */
    <T> List<T> find(Map<String, Object> findValue, Class<T> returnClass);

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

    /**
     * @return 数据源
     */
    String getDataSource();

    /**
     * 获取指定key的对象
     *
     * @param key 指定 key
     * @return 返回对象
     */
    O get(K key);

    /**
     * 批量获取指定key的对象
     *
     * @param keys 指定 key 列表
     * @return 返回对象
     */
    List<O> get(Collection<? extends K> keys);

    /**
     * 插入
     *
     * @param object 存储对象
     * @return 成功返回 true 失败返回 false
     */
    boolean insert(O object);

    /**
     * 批量插入
     *
     * @param objects 存储对象
     * @return 返回失败列表
     */
    Collection<O> insert(Collection<O> objects);

    /**
     * 更新如果存在则更新
     *
     * @param object 对象
     * @return 成功返回 true 失败返回 false
     */
    boolean update(O object);

    /**
     * 批量更新如果存在则更新
     *
     * @param objects 对象列表
     * @return 返回失败列表
     */
    Collection<O> update(Collection<O> objects);

    /**
     * 保存
     *
     * @param object 保存对象
     * @return 成功返回 true 失败返回 false
     */
    boolean save(O object);

    /**
     * 批量保存
     *
     * @param objects 保存对象列表
     * @return 返回失败列表
     */
    Collection<O> save(Collection<O> objects);

    /**
     * 删除
     *
     * @param object 删除对象
     */
    void delete(O object);

    /**
     * 批量删除
     *
     * @param objects 删除对象
     */
    void delete(Collection<O> objects);

}
