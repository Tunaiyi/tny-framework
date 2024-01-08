package com.tny.game.cache.memcached;

import com.schooner.MemCached.MemcachedItem;
import com.tny.game.cache.*;
import com.tny.game.cache.simple.*;
import com.tny.game.common.concurrent.*;
import com.whalin.MemCached.*;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

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
public class MemcachedClient implements CacheClient {

    /**
     * 服务器IP
     */
    private static final String CONFIG_SERVERS = "memcached.conifg.servers";

    /**
     * 权重
     */
    private static final String CONFIG_WEIGHTS = "memcached.conifg.weights";

    /**
     * 初始化连接数
     */
    private static final Object CONFIG_INITCONN = "memcached.conifg.initconn";

    /**
     * 最小连接数
     */
    private static final Object CONFIG_MINCONN = "memcached.conifg.minconn";

    /**
     * 最大连接数
     */
    private static final Object CONFIG_MAXCONN = "memcached.conifg.maxconn";

    /**
     * 空闲时间
     */
    private static final Object CONFIG_MAXIDLE = "memcached.conifg.maxidle";

    /**
     * 主线程的睡眠时间
     */
    private static final Object CONFIG_MAINTSLEEP = "memcached.conifg.maintsleep";

    /**
     * 包缓存
     */
    private static final Object CONFIG_NAGLE = "memcached.conifg.nagle";

    /**
     * 读取超时
     */
    private static final Object CONFIG_SOCKETTO = "memcached.conifg.socketto";

    /**
     * 链接超时时间
     */
    private static final Object CONFIG_CONNECTTO = "memcached.conifg.connectto";

    /**
     * 心跳反映
     */
    private static final Object CONFIG_HEARTBEAT = "memcached.conifg.heartbeat";

    private String name;

    /**
     * 缓存客户端
     */
    private MemCachedClient client = new MemCachedClient();

    /**
     * 心跳线程
     */
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new CoreThreadFactory(
            "Memcache-heart-beat"));

    /**
     * 链接池
     */
    private SockIOPool pool = SockIOPool.getInstance();

    /**
     * 心跳时长
     */
    private int heartbeat = 0;

    @Override
    public String getName() {
        return this.name;
    }

    private String[] getServers(Object serversStr) {
        if (serversStr == null) {
            throw new NullPointerException("Memcached init error cause by serversStr is null");
        }
        return serversStr.toString().split(";");
    }

    private Integer[] getWeights(Object weightsStr) {
        if (weightsStr == null) {
            return new Integer[]{1};
        }
        String[] weightStrs = weightsStr.toString().split(";");
        Integer[] returnArray = new Integer[weightStrs.length];
        for (int index = 0; index < weightStrs.length; index++)
            returnArray[index] = Integer.parseInt(weightStrs[index]);
        return returnArray;
    }

    private int getInteger(Object value, int defaultValue) {
        return value == null ? defaultValue : Integer.parseInt(value.toString());
    }

    private boolean getBoolean(Object value, boolean defaultValue) {
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }

    public MemcachedClient(String path) throws IOException {
        this(null, path);
    }

    public MemcachedClient(String name, String path) throws IOException {
        if (name == null) {
            this.name = "default";
        }
        this.name = name;
        Properties properties = new Properties();
        InputStream inputStream = null;
        final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        inputStream = url.openStream();
        properties.load(inputStream);

        this.pool.setServers(this.getServers(properties.get(MemcachedClient.CONFIG_SERVERS)));
        this.pool.setWeights(this.getWeights(properties.get(MemcachedClient.CONFIG_WEIGHTS)));

        this.pool.setInitConn(this.getInteger(properties.get(MemcachedClient.CONFIG_INITCONN), 5));
        this.pool.setMinConn(this.getInteger(properties.get(MemcachedClient.CONFIG_MINCONN), 5));
        this.pool.setMaxConn(this.getInteger(properties.get(MemcachedClient.CONFIG_MAXCONN), 10));
        this.pool.setMaxIdle(this.getInteger(properties.get(MemcachedClient.CONFIG_MAXIDLE), 1000 * 60 * 60 * 6));
        this.pool.setMaintSleep(this.getInteger(properties.get(MemcachedClient.CONFIG_MAINTSLEEP), 0));

        this.pool.setNagle(this.getBoolean(properties.get(MemcachedClient.CONFIG_NAGLE), false));
        this.pool.setSocketTO(this.getInteger(properties.get(MemcachedClient.CONFIG_SOCKETTO), 30));
        this.pool.setSocketConnectTO(this.getInteger(properties.get(MemcachedClient.CONFIG_CONNECTTO), 0));

        // initialize the connection pool
        this.pool.initialize();
        this.heartbeat = this.getInteger(properties.get(MemcachedClient.CONFIG_HEARTBEAT), 0);
        if (this.heartbeat > 0) {
            this.service.schedule(this.heartBeatTask, this.heartbeat, TimeUnit.SECONDS);
        }
    }

    public Runnable heartBeatTask = new Runnable() {

        @Override
        public void run() {
            MemcachedClient.this.doHeartbeat();
            MemcachedClient.this.service.schedule(this, MemcachedClient.this.heartbeat, TimeUnit.SECONDS);
        }

    };

    private void doHeartbeat() {
        this.get("test");
    }

    public void flushAll() {
        this.client.flushAll();
    }

    @Override
    public void shutdown() {
        this.pool.shutDown();
    }

    @Override
    public Object get(String key) {
        return this.client.get(key);
    }

    @Override
    public Collection<Object> getMultis(Collection<String> keyCollection) {
        if (keyCollection == null || keyCollection.isEmpty()) {
            return Collections.emptyList();
        }
        Object[] objects = this.client.getMultiArray(keyCollection.toArray(new String[0]));
        List<Object> result = null;
        for (Object object : objects) {
            if (object == null) {
                continue;
            }
            result = CacheItemHelper.getAndCreate(result);
            result.add(object);
        }
        return CacheItemHelper.checkEmpty(result);
    }

    @Override
    public Map<String, Object> getMultiMap(Collection<String> keyCollection) {
        if (keyCollection == null || keyCollection.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = this.client.getMulti(keyCollection.toArray(new String[0]));
        if (map == null) {
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public boolean add(String key, Object value, long millisecond) {
        return this.client.add(key, value, new Date(millisecond));
    }

    @Override
    public <C extends CacheItem<?>> List<C> addMultis(Collection<C> cacheItems) {
        List<C> fails = null;
        for (C item : cacheItems) {
            if (!this.client.add(item.getKey(), item.getData(), new Date(item.getExpire()))) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(item);
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public <T> List<CacheItem<T>> addMultis(Map<String, T> valueMap, long millisecond) {
        List<CacheItem<T>> fails = null;
        for (Entry<String, T> entry : valueMap.entrySet()) {
            if (!this.client.add(entry.getKey(), entry.getValue(), new Date(millisecond))) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond));
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public boolean set(String key, Object value, long millisecond) {
        return this.client.set(key, value, new Date(millisecond));
    }

    @Override
    public <C extends CacheItem<?>> List<C> setMultis(Collection<C> cacheItems) {
        List<C> fails = null;
        for (C item : cacheItems) {
            if (!this.client.set(item.getKey(), item.getData(), new Date(item.getExpire()))) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(item);
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public <T> List<CacheItem<T>> setMultis(Map<String, T> valueMap, long millisecond) {
        List<CacheItem<T>> fails = null;
        for (Entry<String, T> entry : valueMap.entrySet()) {
            if (!this.client.set(entry.getKey(), entry.getValue(), new Date(millisecond))) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond));
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public boolean update(String key, Object value, long millisecond) {
        return this.client.replace(key, value, new Date(millisecond));
    }

    @Override
    public <C extends CacheItem<?>> List<C> updateMultis(Collection<C> cacheItems) {
        List<C> fails = null;
        for (C item : cacheItems) {
            if (!this.client.replace(item.getKey(), item.getData(), new Date(item.getExpire()))) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(item);
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public <T> List<CacheItem<T>> updateMultis(Map<String, T> valueMap, long millisecond) {
        List<CacheItem<T>> fails = null;
        for (Entry<String, T> entry : valueMap.entrySet()) {
            if (!this.client.replace(entry.getKey(), entry.getValue(), new Date(millisecond))) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(new SimpleCacheItem<>(entry.getKey(), entry.getValue(), millisecond));
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public boolean delete(String key) {
        return this.client.delete(key);
    }

    @Override
    public List<String> deleteMultis(Collection<String> keys) {
        List<String> fails = null;
        for (String key : keys) {
            if (!this.client.delete(key)) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(key);
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public CasItem<?> gets(String key) {
        MemcachedItem item = this.client.gets(key);
        if (item != null) {
            return new SimpleCasItem<>(key, item.getValue(), item.getCasUnique());
        }
        return null;
    }

    @Override
    public boolean cas(CasItem<?> item, long millisecond) {
        return this.client.cas(item.getKey(), item.getData(), new Date(millisecond), item.getVersion());
    }

}
