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

package com.tny.game.common.collection.map.access;

import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ObjectMap extends HashMap<String, Object> implements TypeMap {

    public ObjectMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ObjectMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ObjectMap() {
    }

    public ObjectMap(Map<String, ?> map) {
        super(map);
    }

    /**
     * 如果 没有则返回 null
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public <T> T getObject(String key) {
        return as(super.get(key));
    }

    @Override
    public <T> T getObject(String key, T defaultValue) {
        // 收口复核：本覆写与 MapAccessor 接口 default 逐字同形，但本类为已发布 public 类，删声明即缩公共面
        // （P11 签名冻结），故保留；其与接口默认实现等价的现状已由 MapConvertAccessContractTest 钉桩。
        T value = getObject(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public String getString(String key) {
        return asObject(key, null, String.class);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return asObject(key, defaultValue, String.class);
    }

    /**
     * getByte 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public byte getByte(String key) {
        return asNotNullObject(key, Byte.class);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return asObject(key, defaultValue, Byte.class);
    }

    /**
     * getInt 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public int getInt(String key) {
        return asNotNullObject(key, Integer.class);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return asObject(key, defaultValue, Integer.class);
    }

    /**
     * getShort 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public short getShort(String key) {
        return asNotNullObject(key, Short.class);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return asObject(key, defaultValue, Short.class);
    }

    /**
     * getLong 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public long getLong(String key) {
        return asNotNullObject(key, Long.class);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return asObject(key, defaultValue, Long.class);
    }

    /**
     * getDouble 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public double getDouble(String key) {
        return asNotNullObject(key, Double.class);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return asObject(key, defaultValue, Double.class);
    }

    /**
     * getFloat 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public float getFloat(String key) {
        return asNotNullObject(key, Float.class);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        // fix-registered-defects D8（task 5.2）：原缺陷实参 float.class 改入 Float.class 与 Wrapper 侧同参；
        // 共享引擎入口另有原始类装箱归一双保险（单一事实源）。CCE 消息尾段分叉按 object-access-conversion
        // 差量归一为装箱形态，MapConvertAccessContractTest.floatDefChannelMessageDriftPinned 翻转钉桩。
        return asObject(key, defaultValue, Float.class);
    }

    /**
     * getBoolean 如果为 Null 抛出异常
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public boolean getBoolean(String key) {
        return asNotNullObject(key, Boolean.class);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return asObject(key, defaultValue, Boolean.class);
    }

    @Override
    public MapAccessor getMapAccessor(String key, MapAccessor defaultValue) {
        Object value = get(key);
        return MapAccessors.cast(value, defaultValue);
    }

    /**
     * 取值链尾段收敛至共享引擎（reduce-code-duplication D4）：取径差异（本类经 getObject(key)，可被子类化覆写）
     * 留在门面，null 判定/缺省/宽松转换单一事实源。
     */
    private <T> T asObject(String key, T defaultValue, Class<T> valueClass) {
        return MapConvertAccessSupport.convertOrNull(getObject(key), defaultValue, valueClass);
    }

    private <T> T asNotNullObject(String key, Class<T> valueClass) {
        return MapConvertAccessSupport.convertRequired(getObject(key), key, valueClass);
    }

    public <T> void getNotNullToFunction(String key, Function<T, ?> function, Class<T> valueClass) {
        MapConvertAccessSupport.convertIfPresent(getObject(key), valueClass, function);
    }

}
