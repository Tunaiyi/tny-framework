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
package com.tny.game.cache.redis;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;

/**
 * @author KGTny
 * @ClassName: MemcachedClient
 * @Description: memcahed封装客户端
 * @date 2011-9-29 下午3:34:08
 * <p>
 * 对memcached客户端进行封装
 * <p>
 * 对memcached客户端进行封装<br>
 */
public class RedisHashCacheClient extends BaseRedisCacheClient {

    private byte[] nameBytes;

    public RedisHashCacheClient(JedisPool client) throws IOException {
        this(null, client);
    }

    public RedisHashCacheClient(String name, JedisPool client) throws IOException {
        super(name, client);
        this.nameBytes = name.getBytes();
    }

    @Override
    protected void jedisFlushDB(Jedis jedis) {
        jedis.del(this.nameBytes);
    }

    @Override
    protected byte[] jedisGet(Jedis jedis, byte[] key) {
        return jedis.hget(this.nameBytes, key);
    }

    @Override
    protected long jedisDel(Jedis jedis, byte[] key) {
        return jedis.hdel(this.nameBytes, key);
    }

    @Override
    protected Response<Long> jedisDel(Pipeline pipeline, byte[] key) {
        return pipeline.hdel(this.nameBytes, key);
    }

    @Override
    protected Response<byte[]> jedisGet(Pipeline pipeline, byte[] key) {
        return pipeline.hget(this.nameBytes, key);
    }

    @Override
    protected List<byte[]> jedisMGet(Jedis jedis, byte[][] key) {
        return jedis.hmget(this.nameBytes, key);
    }

    @Override
    protected boolean jedisSetNX(Jedis jedis, byte[] key, byte[] value) {
        return jedis.hsetnx(this.nameBytes, key, value) > 0;
    }

    @Override
    protected boolean jedisSetNXPX(Jedis jedis, byte[] key, byte[] value, long time) {
        return jedis.hsetnx(this.nameBytes, key, value) > 0;
    }

    @Override
    protected Response<?> jedisSetNX(Pipeline pipeline, byte[] key, byte[] value) {
        return pipeline.hsetnx(this.nameBytes, key, value);
    }

    @Override
    protected Response<?> jedisSetNXPX(Pipeline pipeline, byte[] key, byte[] value, long time) {
        return pipeline.hsetnx(this.nameBytes, key, value);
    }

    @Override
    protected boolean jedisSetXX(Jedis jedis, byte[] key, byte[] value) {
        return jedis.hsetnx(this.nameBytes, key, value) > 0;
    }

    @Override
    protected boolean jedisSetXXPX(Jedis jedis, byte[] key, byte[] value, long time) {
        return jedis.hsetnx(this.nameBytes, key, value) > 0;
    }

    @Override
    protected Response<?> jedisSetXX(Pipeline pipeline, byte[] key, byte[] value) {
        return pipeline.hset(this.nameBytes, key, value);
    }

    @Override
    protected Response<?> jedisSetXXPX(Pipeline pipeline, byte[] key, byte[] value, long time) {
        return pipeline.hset(this.nameBytes, key, value);
    }

    @Override
    protected boolean jedisSet(Jedis jedis, byte[] key, byte[] value) {
        return jedis.hset(this.nameBytes, key, value) > 0;
    }

    @Override
    protected boolean jedisSetPX(Jedis jedis, byte[] key, byte[] value, long time) {
        return jedis.hset(this.nameBytes, key, value) > 0;
    }

    @Override
    protected Response<?> jedisSet(Pipeline pipeline, byte[] key, byte[] value) {
        return pipeline.hset(this.nameBytes, key, value);
    }

    @Override
    protected Response<?> jedisSetPX(Pipeline pipeline, byte[] key, byte[] value, long time) {
        return pipeline.hset(this.nameBytes, key, value);
    }

}
