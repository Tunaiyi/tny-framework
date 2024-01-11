package com.tny.game.cache;

public abstract class RawCacheItem<R, O> implements CacheItem<O> {

    private R rawValue;

    R getRawValue() {
        return rawValue;
    }

    void setRawValue(R rawValue) {
        this.rawValue = rawValue;
    }

}
