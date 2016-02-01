package com.tny.game.cache;

public interface AlterCacheItem<R, O> extends CacheItem<O> {

    public R getRawValue();

    public void setRawValue(R rawValue);

}
