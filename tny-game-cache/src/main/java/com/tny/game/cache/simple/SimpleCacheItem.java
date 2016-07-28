package com.tny.game.cache.simple;

import com.tny.game.cache.RawCacheItem;

public class SimpleCacheItem<T, R> extends RawCacheItem<R, T> {

    private static final long serialVersionUID = 1L;
    private String key;
    private T data;
    private long version;
    private long expire;

    public SimpleCacheItem() {
    }

    public SimpleCacheItem(String key, T value) {
        this(key, value, 0L, -1L);
    }

    public SimpleCacheItem(String key, T value, long millisecond) {
        this(key, value, 0L, millisecond);
    }

    public SimpleCacheItem(String key, T value, long version, long millisecond) {
        this.key = key;
        this.data = value;
        this.version = version;
        this.expire = millisecond;
    }

    @Override
    public long getExpire() {
        return this.expire;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getData() {
        return data;
    }
}
