package com.tny.game.cache.simple;

import com.tny.game.cache.AlterCacheItem;
import com.tny.game.cache.AlterCacheItemFactory;

public class SimpleAlterCacheItemFactory implements AlterCacheItemFactory {

    @Override
    public <T> AlterCacheItem<T, ?> create(String key, Object value, long expire) {
        return new SimpleAlterCacheItem<T, Object>(key, value, expire);
    }

}
