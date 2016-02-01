package com.tny.game.cache.simple;

import com.tny.game.cache.AlterCacheItem;

public class SimpleAlterCacheItem<R, O> extends SimpleCacheItem<O> implements AlterCacheItem<R, O> {

    private static final long serialVersionUID = 1L;
    private R rawValue;

    protected SimpleAlterCacheItem(String key, O value, long millisecond) {
        super(key, value, 0L, millisecond);
    }

    @Override
    public R getRawValue() {
        return this.rawValue;
    }

    @Override
    public void setRawValue(R rawValue) {
        this.rawValue = rawValue;
    }

}
