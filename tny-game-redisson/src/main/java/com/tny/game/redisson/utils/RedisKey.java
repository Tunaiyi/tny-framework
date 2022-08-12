/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.redisson.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 02:01
 */
public class RedisKey {

    private static final String TAG_LEFT = "{";

    private static final String TAG_RIGHT = "}";

    private final String key;

    public static RedisKey keyOf(String key) {
        return new RedisKey(key);
    }

    protected RedisKey(String key) {
        this.key = key;
    }

    /**
     * 点连接, 带分隔符
     *
     * @param keyWord key元素
     * @return 返回 RedisKey
     */
    public RedisKey dot(Object keyWord) {
        return new RedisKey(this.key + RedisKeys.LINK + keyWord);
    }

    /**
     * 点连接, 带分隔符
     *
     * @param keyWords key元素
     * @return 返回 RedisKey
     */
    public RedisKey dot(Object... keyWords) {
        return new RedisKey(this.key + RedisKeys.LINK + StringUtils.join(keyWords, RedisKeys.LINK));
    }

    /**
     * 点连接, 带分隔符
     *
     * @param keyWord key元素
     * @return 返回 RedisKey
     */
    public RedisKey dotWithHashTag(Object keyWord) {
        return new RedisKey(this.key + RedisKeys.LINK + TAG_LEFT + keyWord + TAG_RIGHT);
    }

    public String toKey() {
        return this.key;
    }

    public String toKey(Object value) {
        return this.key + RedisKeys.LINK + value;
    }

    public String toKeyWithHashTag(Object value) {
        return this.key + RedisKeys.LINK + TAG_LEFT + value + TAG_RIGHT;
    }

    public String toKey(Object... values) {
        if (values.length == 1) {
            return toKey(values[0]);
        }
        StringBuilder builder = new StringBuilder(this.key);
        for (Object value : values)
            builder.append(RedisKeys.LINK).append(value);
        return builder.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("key", this.key)
                .toString();
    }

}
