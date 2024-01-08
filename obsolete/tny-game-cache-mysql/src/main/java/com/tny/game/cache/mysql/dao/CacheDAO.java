package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.*;

import java.util.*;

public interface CacheDAO {

    DBCacheItem get(String key);

    Collection<DBCacheItem> get(Collection<String> keys);

    int add(DBCacheItem item);

    int[] add(Collection<? extends DBCacheItem> items);

    int set(DBCacheItem item);

    int[] set(Collection<? extends DBCacheItem> items);

    int update(DBCacheItem item);

    int[] update(Collection<? extends DBCacheItem> items);

    int cas(DBCacheItem item);

    int cas(Collection<? extends DBCacheItem> items);

    int delete(String key);

    int[] delete(Collection<String> keys);

    void flushAll(Object hash);

    List<String> getAllKeys(Object hash);

    List<String> getKeys(long uid, Object hash);

}
