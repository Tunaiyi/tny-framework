package com.tny.game.cache.mysql;

import com.tny.game.cache.AlterCacheItem;
import com.tny.game.cache.AlterCacheItemFactory;

public class DBAlterCacheItemFactory implements AlterCacheItemFactory, DBItemFactory {

    @Override
    public <T> AlterCacheItem<T, ?> create(String key, Object value, long expire) {
        return new AlterDBItem<T>(key, value, 0L, expire);
    }

    @Override
    public DBItem create(String key, Object data, long version, long expire) {
        return new AlterDBItem<Object>(key, data, version, expire);
    }

}
