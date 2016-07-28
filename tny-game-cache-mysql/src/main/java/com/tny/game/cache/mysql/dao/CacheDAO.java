package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.DBCacheItem;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import java.util.Collection;
import java.util.List;

public interface CacheDAO {

    DBCacheItem get(@ShardBy @SQLParam("k") String key);

    Collection<DBCacheItem> get(@ShardBy @SQLParam("k") Collection<String> keys);

    int add(@ShardBy("key") @SQLParam("i") DBCacheItem item);

    int[] add(@ShardBy("key") @SQLParam("i") Collection<? extends DBCacheItem> items);

    int set(@ShardBy("key") @SQLParam("i") DBCacheItem item);

    int[] set(@ShardBy("key") @SQLParam("i") Collection<? extends DBCacheItem> items);

    int update(@ShardBy("key") @SQLParam("i") DBCacheItem item);

    int[] update(@ShardBy("key") @SQLParam("i") Collection<? extends DBCacheItem> items);

    int cas(@ShardBy("key") @SQLParam("i") DBCacheItem item);

    int cas(@ShardBy("key") @SQLParam("i") Collection<? extends DBCacheItem> items);

    int delete(@ShardBy @SQLParam("k") String key);

    int[] delete(@ShardBy @SQLParam("k") Collection<String> keys);

    void flushAll(@ShardBy @SQLParam("hash") Object hash);

    List<String> getAllKeys(@ShardBy @SQLParam("hash") Object hash);

    List<String> getKeys(@SQLParam("uid") long uid, @ShardBy @SQLParam("hash") Object hash);

}
