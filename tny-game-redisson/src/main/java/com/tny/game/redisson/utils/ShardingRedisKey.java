package com.tny.game.redisson.utils;

import com.tny.game.common.utils.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 03:13
 */
public class ShardingRedisKey extends RedisKey {

    private final int shardNumber;

    protected ShardingRedisKey(String key, int shardNumber) {
        super(key);
        this.shardNumber = shardNumber;
    }

    @Override
    public ShardingRedisKey dot(Object keyWord) {
        super.dot(keyWord);
        return this;
    }

    @Override
    public ShardingRedisKey dot(Object... keyWords) {
        super.dot(keyWords);
        return this;
    }

    // @Override
    // public ShardingRedisKey concat(Object word) {
    //     super.concat(word);
    //     return this;
    // }

    @Override
    public ShardingRedisKey toSharding(int shardingNumber) {
        return this;
    }

    @Override
    public String toKey() {
        return super.toKey();
    }

    @Override
    public String toKey(Object value) {
        int hashCode;
        if (value instanceof String) {
            hashCode = HashAide.bkdrStringHash32(value.toString());
        } else {
            hashCode = value.hashCode();
        }
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        return super.toKey(hashCode % this.shardNumber);
    }

}
