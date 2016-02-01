package com.tny.game.cache.redis;

import com.tny.game.cache.CacheClient;
import com.tny.game.cache.CacheHelper;
import com.tny.game.cache.CacheItem;
import com.tny.game.cache.CasItem;
import com.tny.game.cache.simple.SimpleCacheItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
public class RedisClient implements CacheClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);

    private static final String NX = "NX";
    private static final byte[] NX_BYTES = NX.getBytes();
    private static final String XX = "XX";
    private static final byte[] XX_BYTES = XX.getBytes();
    private static final String PX = "PX";
    private static final byte[] PX_BYTES = PX.getBytes();

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

    public RedisClient(JedisPool client) throws IOException {
        this(null, client);
    }

    public RedisClient(String name, JedisPool client) throws IOException {
        if (name == null)
            this.name = "default";
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
            return bytes2Object(jedis.get(key.getBytes()));
        }
    }

    @Override
    public Collection<Object> getMultis(Collection<String> keyCollection) {
        if (keyCollection == null || keyCollection.isEmpty())
            return Collections.emptyList();
        List<byte[]> objects = Collections.emptyList();
        try (Jedis jedis = this.pool.getResource()) {
            objects = jedis.mget(CacheHelper.strings2Bytes(keyCollection));
        }
        List<Object> result = null;
        for (byte[] object : objects) {
            if (object == null)
                continue;
            result = CacheHelper.getAndCreate(result);
            result.add(bytes2Object(object));
        }
        return CacheHelper.checkEmpty(result);
    }

    @Override
    public Map<String, Object> getMultiMap(Collection<String> keyCollection) {
        if (keyCollection == null || keyCollection.isEmpty())
            return Collections.emptyMap();
        Map<String, Response<byte[]>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (String key : keyCollection) {
                Response<byte[]> response = pipeline.get(key.getBytes());
                responseMap.put(key, response);
            }
            pipeline.sync();
        }
        Map<String, Object> result = null;
        for (Entry<String, Response<byte[]>> entry : responseMap.entrySet()) {
            Response<byte[]> response = entry.getValue();
            byte[] object = response.get();
            if (object == null)
                continue;
            result = CacheHelper.getAndCreate(result);
            result.put(entry.getKey(), bytes2Object(object));
        }
        return result;
    }

    @Override
    public boolean add(String key, Object value, long millisecond) {
        try (Jedis jedis = this.pool.getResource()) {
            if (millisecond > 0) {
                return jedis.set(key.getBytes(), object2Bytes(value), NX_BYTES, PX_BYTES, millisecond) != null;
            } else {
                return jedis.set(key.getBytes(), object2Bytes(value), NX_BYTES) != null;
            }
        }
    }

    private Response<String> doAdd(Pipeline pipeline, CacheItem<?> item) {
        if (item.getExpire() > 0) {
            return pipeline.set(item.getKey().getBytes(), object2Bytes(item.getData()), NX_BYTES, PX_BYTES, (int) item.getExpire());
        } else {
            return pipeline.set(item.getKey().getBytes(), object2Bytes(item.getData()), NX_BYTES);
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> addMultis(Collection<C> cacheItems) {
        Map<C, Response<String>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (C item : cacheItems) {
                Response<String> response = doAdd(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public <T> List<CacheItem<T>> addMultis(Map<String, T> valueMap, long millisecond) {
        Map<CacheItem<T>, Response<String>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, T> entry : valueMap.entrySet()) {
                CacheItem<T> item = new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond);
                Response<String> response = doAdd(pipeline, item);
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
                return jedis.setex(key.getBytes(), (int) TimeUnit.MILLISECONDS.toSeconds(millisecond), object2Bytes(value)) != null;
            } else {
                return jedis.set(key.getBytes(), object2Bytes(value)) != null;
            }
        }
    }

    private Response<String> doSet(Pipeline pipeline, CacheItem<?> item) {
        if (item.getExpire() > 0) {
            return pipeline.setex(item.getKey().getBytes(), (int) TimeUnit.MILLISECONDS.toSeconds(item.getExpire()), object2Bytes(item.getData()));
        } else {
            return pipeline.set(item.getKey().getBytes(), object2Bytes(item.getData()));
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> setMultis(Collection<C> cacheItems) {
        Map<C, Response<String>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (C item : cacheItems) {
                Response<String> response = doSet(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public <T> List<CacheItem<T>> setMultis(Map<String, T> valueMap, long millisecond) {
        Map<CacheItem<T>, Response<String>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, T> entry : valueMap.entrySet()) {
                CacheItem<T> item = new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond);
                Response<String> response = doSet(pipeline, item);
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
                return jedis.set(key.getBytes(), object2Bytes(value), XX_BYTES, PX_BYTES, millisecond) != null;
            } else {
                return jedis.set(key.getBytes(), object2Bytes(value), XX_BYTES) != null;
            }
        }
    }

    private Response<String> doUpdate(Pipeline pipeline, CacheItem<?> item) {
        if (item.getExpire() > 0) {
            return pipeline.set(item.getKey().getBytes(), object2Bytes(item.getData()), XX_BYTES, PX_BYTES, (int) item.getExpire());
        } else {
            return pipeline.set(item.getKey().getBytes(), object2Bytes(item.getData()), XX_BYTES);
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> updateMultis(Collection<C> cacheItems) {
        Map<C, Response<String>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (C item : cacheItems) {
                Response<String> response = doUpdate(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public <T> List<CacheItem<T>> updateMultis(Map<String, T> valueMap, long millisecond) {
        Map<CacheItem<T>, Response<String>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Entry<String, T> entry : valueMap.entrySet()) {
                CacheItem<T> item = new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond);
                Response<String> response = doUpdate(pipeline, item);
                responseMap.put(item, response);
            }
            pipeline.sync();
        }
        return checkResponse(responseMap);
    }

    @Override
    public boolean delete(String key) {
        try (Jedis jedis = this.pool.getResource()) {
            return jedis.del(key) > 0;
        }
    }

    @Override
    public List<String> deleteMultis(Collection<String> keys) {
        List<String> fails = null;
        Map<String, Response<Long>> responseMap = new HashMap<>();
        try (Jedis jedis = this.pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (String key : keys) {
                Response<Long> response = pipeline.del(key);
                responseMap.put(key, response);
            }
            pipeline.sync();
        }
        return checkLongResponse(responseMap);
    }

    @Override
    public CasItem<?> gets(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cas(CasItem<?> item, long millisecond) {
        throw new UnsupportedOperationException();
    }

    private <C> List<C> checkLongResponse(Map<C, Response<Long>> responseMap) {
        List<C> fails = null;
        for (Entry<C, Response<Long>> entry : responseMap.entrySet()) {
            Long rs = entry.getValue().get();
            if (rs == null || rs == 0) {
                fails = CacheHelper.getAndCreate(fails);
                fails.add(entry.getKey());
            }
        }
        return CacheHelper.checkEmpty(fails);
    }

    private <C> List<C> checkResponse(Map<C, Response<String>> responseMap) {
        List<C> fails = null;
        for (Entry<C, Response<String>> entry : responseMap.entrySet()) {
            if (entry.getValue().get() == null) {
                fails = CacheHelper.getAndCreate(fails);
                fails.add(entry.getKey());
            }
        }
        return CacheHelper.checkEmpty(fails);
    }


    public static byte[] object2Bytes(Object object) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream objectOut = new ObjectOutputStream(byteOut)) {
            if (object instanceof byte[]) {
                objectOut.writeByte(0);
                objectOut.write((byte[]) object);
            } else {
                objectOut.writeByte(1);
                objectOut.writeObject(object);
            }
            return byteOut.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object bytes2Object(byte[] data) {
        if (data == null)
            return null;
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
             ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {
            byte flag = objectIn.readByte();
            Object result = null;
            if (flag == 0) {
                byte[] value = new byte[objectIn.available()];
                result = objectIn.read(value);
            } else {
                result = objectIn.readObject();
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("item2Object", e);
            return null;
        }
    }


}
