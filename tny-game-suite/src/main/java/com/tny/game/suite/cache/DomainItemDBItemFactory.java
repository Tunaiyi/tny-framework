package com.tny.game.suite.cache;

import com.tny.game.cache.RawCacheItemFactory;

public class DomainItemDBItemFactory<T> implements RawCacheItemFactory<T, DomainDBItem<T>> {

    @Override
    public DomainDBItem<T> create(String key, Object data, long version, long expire) {
        return new DomainDBItem<>(key, data, version, expire);
    }

}
