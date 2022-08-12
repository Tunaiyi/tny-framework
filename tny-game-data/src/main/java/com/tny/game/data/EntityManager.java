/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data;

import java.util.*;

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
        int updateSize = 0;
        for (E entity : entities) {
            if (this.insertEntity(entity)) {
                updateSize++;
            }
        }
        return updateSize;
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
        int updateSize = 0;
        for (E entity : entities) {
            if (this.updateEntity(entity)) {
                updateSize++;
            }
        }
        return updateSize;
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
        int updateSize = 0;
        for (E entity : entities) {
            if (this.saveEntity(entity)) {
                updateSize++;
            }
        }
        return updateSize;
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
        int deleteSize = 0;
        for (E entity : entities) {
            if (this.deleteEntity(entity)) {
                deleteSize++;
            }
        }
        return deleteSize;
    }

}
