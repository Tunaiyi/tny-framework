package com.tny.game.cache;

public interface AlterCacheItemFactory {

    public <T> AlterCacheItem<T, ?> create(String key, Object value, long expire);

}
