/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 12:13 上午
 */
public class MessageParamList extends AbstractList<Object> {

    private final List<Object> paramList;

    public static Optional<MessageParamList> of(Object body) {
        if (body == null) {
            return Optional.empty();
        }
        if (body instanceof MessageParamList) {
            return Optional.of(as(body));
        }
        if (body instanceof Object[]) {
            return Optional.of(new MessageParamList(as(body, Object[].class)));
        }
        if (body instanceof List) {
            return Optional.of(new MessageParamList((List<?>) body));
        }
        return Optional.empty();
    }

    public MessageParamList(List<?> paramList) {
        this.paramList = as(paramList);
    }

    public MessageParamList(Object... params) {
        this.paramList = Arrays.asList(params);
    }

    @Override
    public Object get(int index) {
        return this.paramList.get(index);
    }

    @Override
    public int size() {
        return this.paramList.size();
    }

    /**
     * 如果 没有则返回 null
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */
    public <T> T getObject(int index) {
        return as(this.get(index));
    }

    public <T> T getObject(int index, T defaultValue) {
        T value = getObject(index);
        return value != null ? value : defaultValue;
    }

    public String getString(int index) {
        return asObject(index, null, String.class);
    }

    public String getString(int index, String defaultValue) {
        return asObject(index, defaultValue, String.class);
    }

    /**
     * getByte 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public byte getByte(int index) {
        return asNotNullObject(index, Byte.class);
    }

    public byte getByte(int index, byte defaultValue) {
        return asObject(index, defaultValue, Byte.class);
    }

    /**
     * getInt 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public int getInt(int index) {
        return asNotNullObject(index, Integer.class);
    }

    public int getInt(int index, int defaultValue) {
        return asObject(index, defaultValue, Integer.class);
    }

    /**
     * getShort 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public short getShort(int index) {
        return asNotNullObject(index, Short.class);
    }

    public short getShort(int index, short defaultValue) {
        return asObject(index, defaultValue, Short.class);
    }

    /**
     * getLong 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public long getLong(int index) {
        return asNotNullObject(index, Long.class);
    }

    public long getLong(int index, long defaultValue) {
        return asObject(index, defaultValue, Long.class);
    }

    /**
     * getDouble 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public double getDouble(int index) {
        return asNotNullObject(index, Double.class);
    }

    public double getDouble(int index, double defaultValue) {
        return asObject(index, defaultValue, Double.class);
    }

    /**
     * getFloat 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public float getFloat(int index) {
        return asNotNullObject(index, Float.class);
    }

    public float getFloat(int index, float defaultValue) {
        return asObject(index, defaultValue, float.class);
    }

    /**
     * getBoolean 如果为 Null 抛出异常
     *
     * @param index 查找的 index
     * @return 返回查找的值
     */

    public boolean getBoolean(int index) {
        return asNotNullObject(index, Boolean.class);
    }

    public boolean getBoolean(int index, boolean defaultValue) {
        return asObject(index, defaultValue, Boolean.class);
    }

    private <T> T asObject(int index, T defaultValue, Class<T> valueClass) {
        T value = getObject(index);
        if (value == null) {
            return defaultValue;
        }
        return convertTo(value, valueClass);
    }

    private <T> T asNotNullObject(int index, Class<T> valueClass) {
        Object value = getObject(index);
        if (value == null) {
            throw new NullPointerException("[" + index + "] value is null");
        }
        return convertTo(value, valueClass);
    }

    public <T> void getNotNullToFunction(int index, Function<T, ?> function, Class<T> valueClass) {
        Object value = getObject(index);
        if (value != null) {
            function.apply(convertTo(value, valueClass));
        }
    }

}
