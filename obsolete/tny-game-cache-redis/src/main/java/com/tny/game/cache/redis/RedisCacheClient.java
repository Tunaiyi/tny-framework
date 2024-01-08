package com.tny.game.cache.redis;

import com.tny.game.cache.*;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

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
public class RedisCacheClient extends BaseRedisCacheClient {

    public RedisCacheClient(JedisPool client) throws IOException {
        this(null, client);
    }

    public RedisCacheClient(String name, JedisPool client) throws IOException {
        super(name, client);
    }

    @Override
    public CasItem<?> gets(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cas(CasItem<?> item, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void jedisFlushDB(Jedis jedis) {
        jedis.flushDB();
    }

    @Override
    protected byte[] jedisGet(Jedis jedis, byte[] key) {
        return jedis.get(key);
    }

    @Override
    protected long jedisDel(Jedis jedis, byte[] key) {
        return jedis.del(key);
    }

    @Override
    protected Response<Long> jedisDel(Pipeline pipeline, byte[] key) {
        return pipeline.del(key);
    }

    @Override
    protected Response<byte[]> jedisGet(Pipeline pipeline, byte[] key) {
        return pipeline.get(key);
    }

    @Override
    protected List<byte[]> jedisMGet(Jedis jedis, byte[][] key) {
        return jedis.mget(key);
    }

    @Override
    protected boolean jedisSetNX(Jedis jedis, byte[] key, byte[] value) {
        return jedis.set(key, value, SetParams.setParams().nx()) != null;
    }

    @Override
    protected boolean jedisSetNXPX(Jedis jedis, byte[] key, byte[] value, long time) {
        return jedis.set(key, value, SetParams.setParams().nx().px(time)) != null;
    }

    @Override
    protected Response<?> jedisSetNX(Pipeline pipeline, byte[] key, byte[] value) {
        return pipeline.set(key, value, NX_PARAMS);
    }

    @Override
    protected Response<?> jedisSetNXPX(Pipeline pipeline, byte[] key, byte[] value, long time) {
        return pipeline.set(key, value, SetParams.setParams().nx().px(time));
    }

    @Override
    protected boolean jedisSetXX(Jedis jedis, byte[] key, byte[] value) {
        return jedis.set(key, value, XX_PARAMS) != null;
    }

    @Override
    protected boolean jedisSetXXPX(Jedis jedis, byte[] key, byte[] value, long time) {
        return jedis.set(key, value, SetParams.setParams().nx().px(time)) != null;
    }

    @Override
    protected Response<?> jedisSetXX(Pipeline pipeline, byte[] key, byte[] value) {
        return pipeline.set(key, value, XX_PARAMS);
    }

    @Override
    protected Response<?> jedisSetXXPX(Pipeline pipeline, byte[] key, byte[] value, long time) {
        return pipeline.set(key, value, SetParams.setParams().nx().px(time));
    }

    @Override
    protected boolean jedisSet(Jedis jedis, byte[] key, byte[] value) {
        return jedis.set(key, value) != null;
    }

    @Override
    protected boolean jedisSetPX(Jedis jedis, byte[] key, byte[] value, long time) {
        return jedis.psetex(key, time, value) != null;
    }

    @Override
    protected Response<?> jedisSet(Pipeline pipeline, byte[] key, byte[] value) {
        return pipeline.set(key, value);
    }

    @Override
    protected Response<?> jedisSetPX(Pipeline pipeline, byte[] key, byte[] value, long time) {
        return pipeline.psetex(key, time, value);
    }

}
