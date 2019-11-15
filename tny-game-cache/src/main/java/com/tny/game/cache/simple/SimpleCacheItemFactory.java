package com.tny.game.cache.simple;

import com.tny.game.cache.*;

public class SimpleCacheItemFactory<T> implements RawCacheItemFactory<T, RawCacheItem<T, ?>> {

    @Override
    public RawCacheItem<T, ?> create(String key, Object value, long version, long expire) {
        return new SimpleCacheItem<>(key, value, version, expire);
    }

}
