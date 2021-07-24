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
        Object value = this.getObject(key);
        return MapAccessors.cast(value, defaultValue);
    }

    @Override
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public int size() {
        return 0;
    }

    private <T> T asObject(String key, T defaultValue, Class<T> valueClass) {
        T value = getObject(key);
        if (value == null) {
            return defaultValue;
        }
        return as(value, valueClass);
    }

    private <T> T asNotNullObject(String key, Class<T> valueClass) {
        Object value = getObject(key);
        if (value == null) {
            throw new NullPointerException("[" + key + "] value is null");
        }
        return as(value, valueClass);
    }

    public <T> void getNotNullToFunction(String key, Function<T, ?> function, Class<T> valueClass) {
        Object value = getObject(key);
        if (value != null) {
            function.apply(as(value, valueClass));
        }
    }

}
