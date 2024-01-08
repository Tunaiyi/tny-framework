package com.tny.game.cache.redis;

import com.tny.game.cache.*;
import com.tny.game.cache.simple.*;
import org.slf4j.*;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import java.io.*;
import java.sql.Blob;
import java.util.*;
import java.util.Map.Entry;

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
public abstract class BaseRedisCacheClient implements CacheClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseRedisCacheClient.class);

    protected static final SetParams NX_PARAMS = SetParams.setParams().nx();

    protected static final SetParams XX_PARAMS = SetParams.setParams().xx();

    private String name;

    /**
     * 缓存客户端
     */
    private JedisPool pool = new JedisPool();

    //	/** 心跳时长 */
    //	private int heartbeat = 0;

    @Override
    public String getName() {
        return this.name;
    }

    public BaseRedisCacheClient(JedisPool client) throws IOException {
        this(null, client);
    }

    public BaseRedisCacheClient(String name, JedisPool client) throws IOException {
        if (name == null) {
            this.name = "default";
        }
        this.name = name;
        this.pool = client;
    }

    public void flushAll() {
        try (Jedis jedis = this.pool.getResource()) {
            jedis.flushDB();
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public Object get(String key) {
        try (Jedis jedis = this.pool.getResource()) {
            return bytes2Object(jedisGet(jedis, key.getBytes()));
        }
    }

    @Override
    public Collection<Object> getMultis(Collection<String> keyCollection) {
        if (keyCollection == null || keyCollection.isEmpty()) {
            return Collections.emptyList();
        }
        List<byte[]> objects;
        try (Jedis jedis = this.pool.getResource()) {
            objects = jedisMGet(jedis, CacheItemHelper.strings2Bytes(keyCollection));
        }
        List<Object> result = null;
        for (byte[] object : objects) {
            if (object == null) {
                continue;
            }
            result = CacheItemHelper.getAndCreate(result);
            result.add(bytes2Object(object));
        }
        return CacheItemHelper.checkEmpty(result);
    }

    @Override
    public Map<String, Object> getMultiMap(Collection<String> keyCollection) {
        if (keyCollection == null || keyCollection.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Response<byte[]>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (String key : keyCollection) {
                Response<byte[]> response = jedisGet(pipeline, key.getBytes());
                responseMap.put(key, response);
            }
            pipeline.sync();
        }
        Map<String, Object> result = null;
        for (Entry<String, Response<byte[]>> entry : responseMap.entrySet()) {
            Response<byte[]> response = entry.getValue();
            byte[] object = response.get();
            if (object == null) {
                continue;
            }
            result = CacheItemHelper.getAndCreate(result);
            result.put(entry.getKey(), bytes2Object(object));
        }
        return result;
    }

    @Override
    public boolean add(String key, Object value, long millisecond) {
        try (Jedis jedis = this.pool.getResource()) {
            if (millisecond > 0) {
                return jedisSetNXPX(jedis, key.getBytes(), object2Bytes(value), millisecond);
            } else {
                return jedisSetNX(jedis, key.getBytes(), object2Bytes(value));
            }
        }
    }

    private Response<?> doAdd(Pipeline pipeline, CacheItem<?> item) {
        if (item.getExpire() > 0) {
            return jedisSetNXPX(pipeline, item.getKey().getBytes(), object2Bytes(item.getData()), (int) item.getExpire());
        } else {
            return jedisSetNX(pipeline, item.getKey().getBytes(), object2Bytes(item.getData()));
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> addMultis(Collection<C> cacheItems) {
        Map<C, Response<?>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (C item : cacheItems) {
                Response<?> response = doAdd(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public <T> List<CacheItem<T>> addMultis(Map<String, T> valueMap, long millisecond) {
        Map<CacheItem<T>, Response<?>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, T> entry : valueMap.entrySet()) {
                CacheItem<T> item = new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond);
                Response<?> response = doAdd(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public boolean set(String key, Object value, long millisecond) {
        try (Jedis jedis = this.pool.getResource()) {
            if (millisecond > 0) {
                return jedisSetPX(jedis, key.getBytes(), object2Bytes(value), millisecond);
            } else {
                return jedisSet(jedis, key.getBytes(), object2Bytes(value));
            }
        }
    }

    private Response<?> doSet(Pipeline pipeline, CacheItem<?> item) {
        if (item.getExpire() > 0) {
            return jedisSetPX(pipeline, item.getKey().getBytes(), object2Bytes(item.getData()), item.getExpire());
        } else {
            return jedisSet(pipeline, item.getKey().getBytes(), object2Bytes(item.getData()));
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> setMultis(Collection<C> cacheItems) {
        Map<C, Response<?>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (C item : cacheItems) {
                Response<?> response = doSet(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public <T> List<CacheItem<T>> setMultis(Map<String, T> valueMap, long millisecond) {
        Map<CacheItem<T>, Response<?>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, T> entry : valueMap.entrySet()) {
                CacheItem<T> item = new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond);
                Response<?> response = doSet(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public boolean update(String key, Object value, long millisecond) {
        try (Jedis jedis = this.pool.getResource()) {
            if (millisecond > 0) {
                return jedisSetXXPX(jedis, key.getBytes(), object2Bytes(value), millisecond);
            } else {
                return jedisSetXX(jedis, key.getBytes(), object2Bytes(value));
            }
        }
    }

    private Response<?> doUpdate(Pipeline pipeline, CacheItem<?> item) {
        if (item.getExpire() > 0) {
            return jedisSetXXPX(pipeline, item.getKey().getBytes(), object2Bytes(item.getData()), item.getExpire());
        } else {
            return jedisSetXX(pipeline, item.getKey().getBytes(), object2Bytes(item.getData()));
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> updateMultis(Collection<C> cacheItems) {
        Map<C, Response<?>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (C item : cacheItems) {
                Response<?> response = doUpdate(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public <T> List<CacheItem<T>> updateMultis(Map<String, T> valueMap, long millisecond) {
        Map<CacheItem<T>, Response<?>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, T> entry : valueMap.entrySet()) {
                CacheItem<T> item = new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond);
                Response<?> response = doUpdate(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public boolean delete(String key) {
        try (Jedis jedis = this.pool.getResource()) {
            return jedisDel(jedis, key.getBytes()) > 0;
        }
    }

    @Override
    public List<String> deleteMultis(Collection<String> keys) {
        Map<String, Response<Long>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (String key : keys) {
                Response<Long> response = jedisDel(pipeline, key.getBytes());
                responseMap.put(key, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public CasItem<?> gets(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cas(CasItem<?> item, long millisecond) {
        throw new UnsupportedOperationException();
    }

    private <C> List<C> checkResponse(Map<C, ? extends Response<?>> responseMap) {
        List<C> fails = null;
        for (Entry<C, ? extends Response<?>> entry : responseMap.entrySet()) {
            Object rs = entry.getValue().get();
            if (rs == null || (rs instanceof Long && (Long) rs <= 0)) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(entry.getKey());
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    protected abstract void jedisFlushDB(Jedis jedis);

    protected abstract byte[] jedisGet(Jedis jedis, byte[] key);

    protected abstract long jedisDel(Jedis jedis, byte[] key);

    protected abstract Response<Long> jedisDel(Pipeline pipeline, byte[] key);

    protected abstract Response<byte[]> jedisGet(Pipeline pipeline, byte[] key);

    protected abstract List<byte[]> jedisMGet(Jedis jedis, byte[][] key);

    protected abstract boolean jedisSetNX(Jedis jedis, byte[] key, byte[] value);

    protected abstract boolean jedisSetNXPX(Jedis jedis, byte[] key, byte[] value, long time);

    protected abstract Response<?> jedisSetNX(Pipeline pipeline, byte[] key, byte[] value);

    protected abstract Response<?> jedisSetNXPX(Pipeline pipeline, byte[] key, byte[] value, long time);

    protected abstract boolean jedisSetXX(Jedis jedis, byte[] key, byte[] value);

    protected abstract boolean jedisSetXXPX(Jedis jedis, byte[] key, byte[] value, long time);

    protected abstract Response<?> jedisSetXX(Pipeline pipeline, byte[] key, byte[] value);

    protected abstract Response<?> jedisSetXXPX(Pipeline pipeline, byte[] key, byte[] value, long time);

    protected abstract boolean jedisSet(Jedis jedis, byte[] key, byte[] value);

    protected abstract boolean jedisSetPX(Jedis jedis, byte[] key, byte[] value, long time);

    protected abstract Response<?> jedisSet(Pipeline pipeline, byte[] key, byte[] value);

    protected abstract Response<?> jedisSetPX(Pipeline pipeline, byte[] key, byte[] value, long time);

    private static byte[] object2Bytes(Object object) {
        try {
            if (object instanceof byte[]) {
                byte[] bytes = (byte[]) object;
                byte[] data = new byte[bytes.length + 1];
                data[0] = 0;
                System.arraycopy(bytes, 0, data, 1, bytes.length);
                return data;
            } else if (object instanceof Blob) {
                Blob blob = (Blob) object;
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                byte[] data = new byte[bytes.length + 1];
                data[0] = 0;
                System.arraycopy(bytes, 0, data, 1, bytes.length);
                return data;
            } else {
                try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                     ObjectOutputStream objectOut = new ObjectOutputStream(byteOut)) {
                    objectOut.writeByte(1);
                    objectOut.writeObject(object);
                    objectOut.flush();
                    return byteOut.toByteArray();
                }
            }
        } catch (Throwable e) {
            LOGGER.error("object2Bytes", e);
            return null;
        }

    }

    private static Object bytes2Object(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            byte flag = data[0];
            if (flag == 0) {
                byte[] bytes = new byte[data.length - 1];
                System.arraycopy(data, 1, bytes, 0, bytes.length);
                return bytes;
            } else {
                try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
                     ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {
                    return objectIn.readObject();
                }
            }
        } catch (Exception e) {
            LOGGER.error("item2Object", e);
            return null;
        }
    }

}
