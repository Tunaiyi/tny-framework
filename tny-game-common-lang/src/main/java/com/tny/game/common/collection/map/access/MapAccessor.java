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

package com.tny.game.common.collection.map.access;

import java.util.Map;
import java.util.function.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/11 12:41 下午
 */
public interface MapAccessor {

    /**
     * 通过 key 获取 getObject
     *
     * @param key 指定key
     * @param <T> 返回类型
     * @return 返回值, 如果不存在返回 null
     */
    <T> T getObject(String key);

    /**
     * 通过 key 获取 getObject, 如果不存在调用 defaultValueSupplier 获取默认值
     *
     * @param key                  指定key
     * @param defaultValueSupplier 默认值提供器
     * @param <T>                  返回类型
     * @return 返回值
     */
    default <T> T getObject(String key, Supplier<T> defaultValueSupplier) {
        T value = getObject(key);
        if (value == null) {
            return defaultValueSupplier.get();
        }
        return value;
    }

    /**
     * 通过 key 获取 getObject, 如果key存在value, 则代用 handler 处理
     *
     * @param key     指定key
     * @param handler 处理器
     * @param <T>     返回类型
     * @return 返回值, 如果不存在返回 null
     */
    default <T> T getObject(String key, Function<String, T> handler) {
        T value = getObject(key);
        if (value == null) {
            return handler.apply(key);
        }
        return value;
    }

    /**
     * 通过 key 获取 getObject, 如果不存在则返回 defaultValue
     *
     * @param key          指定key
     * @param defaultValue 默认值
     * @param <T>          返回类型
     * @return 返回值, 如果不存在返回 null
     */
    default <T> T getObject(String key, T defaultValue) {
        T value = getObject(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 通过 key 获取 getObject, 如果不存在则抛出 NullPointerException 异常
     *
     * @param key 指定key
     * @param <T> 返回类型
     * @return 返回值, 如果不存抛 NullPointerException
     */
    default <T> T getAndCheckObject(String key) {
        T value = getObject(key);
        if (value == null) {
            throw new NullPointerException("[" + key + "] value is null");
        }
        return value;
    }

    /**
     * 通过指定 key 获取 String 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    String getString(String key);

    /**
     * 通过指定 key 获取 String 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    String getString(String key, String defaultValue);

    /**
     * 通过指定 key 获取 Byte 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    byte getByte(String key);

    /**
     * 通过指定 key 获取 Byte 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    byte getByte(String key, byte defaultValue);

    /**
     * 通过指定 key 获取 Int 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    int getInt(String key);

    /**
     * 通过指定 key 获取 Int 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    int getInt(String key, int defaultValue);

    /**
     * 通过指定 key 获取 Short 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    short getShort(String key);

    /**
     * 通过指定 key 获取 Short 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    short getShort(String key, short defaultValue);

    /**
     * 通过指定 key 获取 Long 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    long getLong(String key);

    /**
     * 通过指定 key 获取 Long 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    long getLong(String key, long defaultValue);

    /**
     * 通过指定 key 获取 Double 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    double getDouble(String key);

    /**
     * 通过指定 key 获取 Double 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    double getDouble(String key, double defaultValue);

    /**
     * 通过指定 key 获取 Float 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    float getFloat(String key);

    /**
     * 通过指定 key 获取 Float 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    float getFloat(String key, float defaultValue);

    /**
     * 通过指定 key 获取 Boolean 值,  如果值为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    boolean getBoolean(String key);

    /**
     * 通过指定 key 获取 Boolean 值,  如果值为 Null 则返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回查找的值
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * 通过指定 key 获取 MapAccessor 值, 如果不存在则返回 null
     *
     * @param key 查找的 key
     * @return 返回 MapAccessor
     */
    default MapAccessor getMapAccessor(String key) {
        return getMapAccessor(key, null);
    }

    /**
     * 通过指定 key 获取 MapAccessor 值, 如果返回默认值
     *
     * @param key          查找的 key
     * @param defaultValue 默认值
     * @return 返回 MapAccessor
     */
    default MapAccessor getMapAccessor(String key, MapAccessor defaultValue) {
        Object value = getObject(key);
        return MapAccessors.cast(value, defaultValue);
    }

    /**
     * 转变为 map
     *
     * @return 返回转变的 map
     */
    Map<String, Object> toMap();

    /**
     * @return map 长度
     */
    int size();

}