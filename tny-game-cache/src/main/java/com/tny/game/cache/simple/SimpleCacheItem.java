package com.tny.game.cache.simple;

import com.tny.game.cache.CacheItem;

public class SimpleCacheItem<T> extends SimpleCasItem<T> implements CacheItem<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long expire;

    public SimpleCacheItem() {
    }

    ;

    public SimpleCacheItem(String key, T value) {
        this(key, value, 0L, -1L);
    }

    public SimpleCacheItem(String key, T value, long millisecond) {
        this(key, value, 0L, millisecond);
    }

    public SimpleCacheItem(String key, T value, long vesion, long millisecond) {
        super(key, value, vesion);
        this.expire = millisecond;
    }

    public long getExpire() {
        return this.expire;
    }

}
