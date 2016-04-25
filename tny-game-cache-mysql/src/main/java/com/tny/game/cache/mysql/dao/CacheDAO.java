package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.DBItem;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import java.util.Collection;
import java.util.List;

public interface CacheDAO {

    DBItem get(@ShardBy @SQLParam("k") String key);

    Collection<DBItem> get(@ShardBy @SQLParam("k") Collection<String> keys);

    int add(@ShardBy("key") @SQLParam("i") DBItem item);

    int[] add(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    int set(@ShardBy("key") @SQLParam("i") DBItem item);

    int[] set(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    int update(@ShardBy("key") @SQLParam("i") DBItem item);

    int[] update(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    int cas(@ShardBy("key") @SQLParam("i") DBItem item);

    int cas(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    int delete(@ShardBy @SQLParam("k") String key);

    int[] delete(@ShardBy @SQLParam("k") Collection<String> keys);

    void flushAll(@ShardBy @SQLParam("hash") Object hash);

    List<String> getAllKeys(@ShardBy @SQLParam("hash") Object hash);

    List<String> getKeys(@SQLParam("uid") long uid, @ShardBy @SQLParam("hash") Object hash);

}
