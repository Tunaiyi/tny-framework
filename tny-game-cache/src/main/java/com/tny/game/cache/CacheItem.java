package com.tny.game.cache;

public interface CacheItem<T> extends CasItem<T> {

    long getExpire();

}
