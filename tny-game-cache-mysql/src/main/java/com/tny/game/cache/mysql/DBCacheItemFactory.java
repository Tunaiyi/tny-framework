package com.tny.game.cache.mysql;

import com.tny.game.cache.*;

/**
 * Created by Kun Yang on 16/7/26.
 */
public class DBCacheItemFactory<T> implements RawCacheItemFactory<T, DBCacheItem<T>> {

    @Override
    public DBCacheItem<T> create(String key, Object value, long version, long expire) {
        return new DBCacheItem<>(key, value, version, expire);
    }

}
