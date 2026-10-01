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
        // D4 遗留登记（禁止顺手修）：recon 期现状为 float.class 致值存在必抛 CCE；基线树经 ObjectAide.convertTo
        // 基本类型分支消化为正常返回。缺陷实参原样保留（与 Wrapper 侧 Float.class 的差异由参数承载，单一事实源），
        // 不可转换格 CCE 消息尾段 "float" vs "class java.lang.Float" 分叉已由 MapConvertAccessContractTest 逐字钉死。
        return asObject(key, defaultValue, float.class);
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
