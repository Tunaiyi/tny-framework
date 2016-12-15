package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.DBCacheItem;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import java.util.Collection;
import java.util.List;

@DAO
public interface JadeCacheDAO extends CacheDAO, ShardCacheDAO {

    String SEPARATOR = ":";
    String KEY_TABLE = "`##(:k.substring(0, :k.indexOf($SEPARATOR)))`";
    String ITEM_TABLE = "`##(:i.key.substring(0, :i.key.indexOf($SEPARATOR)))`";
    String FIELD = "`key`, `flags`, `data`, `expire`, `saveAt`";
    String FIELD_VERSION = "`key`, `flags`, `data`, `expire`, `version`";

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + KEY_TABLE + " WHERE `key` = :k")
    DBCacheItem get(@SQLParam("k") String key);

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + KEY_TABLE + " WHERE `key` IN (:k)")
    Collection<DBCacheItem> get(@ShardBy @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int add(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int[] add(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("REPLACE INTO " + ITEM_TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int set(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("REPLACE INTO " + ITEM_TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int[] set(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("UPDATE " + ITEM_TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt WHERE `key` = :i.key")
    int update(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("UPDATE " + ITEM_TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt WHERE `key` = :i.key")
    int[] update(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("UPDATE " + ITEM_TABLE + " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `version`=:i.version + 1 WHERE `key` = :i.key and `version` = :i.version")
    int cas(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("UPDATE " + ITEM_TABLE + " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `version`=:i.version + 1 WHERE `key` = :i.key and `version` = :i.version")
    int cas(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("DELETE FROM " + KEY_TABLE + " WHERE `key` = :k")
    int delete(@SQLParam("k") String key);

    @Override
    @SQL("DELETE FROM " + KEY_TABLE + " WHERE `key` = :k")
    int[] delete(@ShardBy @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("TRUNCATE TABLE ##(:hash)")
    void flushAll(@SQLParam("hash") Object hash);

    @Override
    @SQL("SELECT `key` FROM ##(:hash)")
    List<String> getAllKeys(@SQLParam("hash") Object hash);

    @Override
    @SQL("SELECT `key` FROM ##(:hash )WHERE `UID` = :uid")
    List<String> getKeys(@SQLParam("uid") long uid, @SQLParam("hash") Object hash);

}
