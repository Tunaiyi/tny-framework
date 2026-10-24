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

package com.tny.game.data;

import java.util.*;
import java.util.function.ToIntFunction;

/**
 * <p>
 */
public interface EntityManager<K extends Comparable<?>, E> {

    /**
     * 按索引字段查找实体类
     *
     * @param query 查询字段
     * @return 返回查找信息
     */
    default List<E> find(Map<String, Object> query) {
        return find(query, null);
    }

    /**
     * 按索引字段查找实体类
     *
     * @param query  查询字段
     * @param onLoad 加载回调
     * @return 返回查找信息
     */
    List<E> find(Map<String, Object> query, EntityOnLoad<K, E> onLoad);

    /**
     * 查找所有实体类
     *
     * @param onLoad 加载回调
     * @return 返回查找信息
     */
    List<E> findAll(EntityOnLoad<K, E> onLoad);

    /**
     * 查找所有实体类
     *
     * @return 返回查找信息
     */
    default List<E> findAll() {
        return findAll(null);
    }

    /**
     * 加载, 如果没有则创建并插入
     *
     * @param id      id
     * @param creator 实体工厂
     */
    default E loadEntity(K id, EntityCreator<K, E> creator) {
        return loadEntity(id, creator, null);
    }

    /**
     * 加载, 如果没有则创建并插入
     *
     * @param id      id
     * @param onLoad  加载回调
     * @param creator 实体工厂
     */
    E loadEntity(K id, EntityCreator<K, E> creator, EntityOnLoad<K, E> load);

    /**
     * 获取指定id的实体
     *
     * @param id id
     * @return 返回获取实体
     */
    default E getEntity(K id) {
        return getEntity(id, null);
    }

    /**
     * 获取指定id的实体
     *
     * @param id     id
     * @param onLoad 加载回调
     * @return 返回获取实体
     */
    E getEntity(K id, EntityOnLoad<K, E> onLoad);

    /**
     * 批量获取指定id的实体
     *
     * @param id id列表
     * @return 返回获取实体
     */
    default List<E> getEntities(List<K> id) {
        return getEntities(id, null);
    }

    /**
     * 批量获取指定id的实体
     *
     * @param idList id列表
     * @param onLoad 加载回调
     * @return 返回获取实体
     */
    List<E> getEntities(List<K> idList, EntityOnLoad<K, E> onLoad);

    /**
     * 批量获取实体
     *
     * @param keys 指定 key 列表
     * @return 返回 key
     */
    default List<E> getEntities(Collection<K> keys) {
        return getEntities(keys, null);
    }

    /**
     * 批量获取实体
     *
     * @param keys 指定 key 列表
     * @return 返回 key
     */
    default List<E> getEntities(Collection<K> keys, EntityOnLoad<K, E> onLoad) {
        List<E> entities = new ArrayList<>();
        for (K key : keys) {
            E value = getEntity(key, onLoad);
            if (value != null) {
                entities.add(value);
            }
        }
        return entities;
    }

    /**
     * 插入实体
     *
     * @param entities 实体
     * @return 返回实体
     */
    boolean insertEntity(E entities);

    /**
     * 插入实体
     *
     * @param entities 实体
     * @return 返回实体
     */
    default int insertEntities(Collection<E> entities) {
        return applyEach(entities, entity -> this.insertEntity(entity) ? 1 : 0);
    }

    /**
     * 更新指定实体
     *
     * @param entities 对象
     * @return 返回更新成功
     */
    boolean updateEntity(E entities);

    /**
     * 更新指定实体
     *
     * @param entities 对象
     * @return 返回更新成功
     */
    default int updateEntities(Collection<E> entities) {
        return applyEach(entities, entity -> this.updateEntity(entity) ? 1 : 0);
    }

    /**
     * 保存(无则插入有则更新)指定实体
     *
     * @param entities 对象
     * @return 是否保存成功
     */
    boolean saveEntity(E entities);

    /**
     * 批量保存(无则插入有则更新)指定实体
     *
     * @param entities 对象
     * @return 返回更新成功
     */
    default int saveEntities(Collection<E> entities) {
        return applyEach(entities, entity -> this.saveEntity(entity) ? 1 : 0);
    }

    /**
     * 删除指定 id 的对象
     *
     * @param id id
     * @return 返回移除对象
     */
    boolean deleteEntity(E object);

    /**
     * 批量保存(无则插入有则更新)指定实体
     *
     * @param entities 对象
     * @return 返回更新成功
     */
    default int deleteEntities(Collection<E> entities) {
        return applyEach(entities, entity -> this.deleteEntity(entity) ? 1 : 0);
    }

    /**
     * 批量 default 方法共享的逐元素累加骨架（reduce-code-duplication D8：四段同形 for 循实体→计次岛收敛单实现）。
     * <p>
     * 行为逐字等价于原四方法体内联循环：按集合迭代序对每个实体调用 {@code counter}、累加返回值、
     * 空集合返回 0、null 集合按现状在 for-each 处抛 NullPointerException（禁止顺手修）。
     * 四个批量 default 经 0/1 计数 lambda 传入，累加和即成功条数。
     *
     * @param entities 实体集合
     * @param counter  单实体计次函数（成功 1、失败 0）
     * @return 返回累加计数
     */
    default int applyEach(Collection<E> entities, ToIntFunction<E> counter) {
        int updateSize = 0;
        for (E entity : entities) {
            updateSize += counter.applyAsInt(entity);
        }
        return updateSize;
    }

}
