package com.tny.game.redisson.utils;

import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 02:01
 */
public class RedisKey {

    public static final String TAG_LEFT = "{";

    public static final String TAG_RIGHT = "}";

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

    // /**
    //  * 连接, 不带连接符
    //  *
    //  * @param word 追加内容
    //  * @return 返回 RedisKey
    //  */
    // public RedisKey concat(Object word) {
    //     return new RedisKey(this.key + LINK + word);
    // }

    public ShardingRedisKey toSharding(int shardingNumber) {
        Asserts.checkArgument(shardingNumber > 0, "{} shardingNumber < 0", this);
        return new ShardingRedisKey(this.key, shardingNumber);
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
