package com.tny.game.cache;

public interface RawCacheItemFactory<T, I extends RawCacheItem<T, ?>> {

    I create(String key, Object value, long version, long expire);

}
