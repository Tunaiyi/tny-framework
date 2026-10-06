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
 *
 * @author Kun Yang
 * @date 2021/5/11 12:44 下午
 */
class WrapperObjectMap implements MapAccessor {

    private final Map<String, Object> map;

    public WrapperObjectMap() {
        this.map = Collections.emptyMap();
    }

    public WrapperObjectMap(Map<String, ?> map) {
        this.map = as(map);
    }

    /**
     * 如果 没有则返回 null
     *
     * @param key 查找的 key
     * @return 返回查找的值
     */
    @Override
    public <T> T getObject(String key) {
        return as(this.map.get(key));
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
     * getDouble 如果为 Null 抛出异常
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
        // 原传 float.class（fix-registered-defects D8 翻转前现状）：基本类型 Class 的 isInstance 恒 false，值存在时必抛 CCE
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

    // 收口压缩：getMapAccessor(key, def) 本类覆写体与 MapAccessor 接口 default 逐字同形（仅 this. 前缀之差），
    // 已删由接口共享既有形态承载——分派 invokeinterface → 接口 default → this.getObject(key) → 本类 getObject
    // （= as(map.get(key))），与原覆写逐路径等值；MapConvertAccessContractTest.mapAccessorChannelAgrees 钉桩。

    @Override
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    /**
     * 取值链尾段收敛至共享引擎（reduce-code-duplication D4）：取径差异（本类经 map.get(key) 直取）留在门面，
     * null 判定/缺省/宽松转换单一事实源；D8（fix-registered-defects）后引擎入口统一装箱归一，两侧实参同为 Float.class、失败消息尾段同形。
     */
    private <T> T asObject(String key, T defaultValue, Class<T> valueClass) {
        // 与同契约非包装实现统一：类型命中直取，错型走宽松转换（数字串/数值窄化/toString）
        return MapConvertAccessSupport.convertOrNull(this.map.get(key), defaultValue, valueClass);
    }

    private <T> T asNotNullObject(String key, Class<T> valueClass) {
        return MapConvertAccessSupport.convertRequired(this.map.get(key), key, valueClass);
    }

    public <T> void getNotNullToFunction(String key, Function<T, ?> function, Class<T> valueClass) {
        MapConvertAccessSupport.convertIfPresent(this.map.get(key), valueClass, function);
    }

}
