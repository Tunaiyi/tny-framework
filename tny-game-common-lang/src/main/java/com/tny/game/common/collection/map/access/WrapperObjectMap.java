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
        // 原传 float.class：基本类型 Class 的 isInstance 恒 false，值存在时必抛 CCE
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
     * null 判定/缺省/宽松转换单一事实源；Float.class 实参（fix 轮已修侧）与 ObjectMap 侧 float.class 的差异由参数承载。
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
