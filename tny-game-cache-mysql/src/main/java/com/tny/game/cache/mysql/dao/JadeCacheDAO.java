package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.DBItem;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import java.util.Collection;
import java.util.List;

@DAO
public interface JadeCacheDAO extends CacheDAO, ShardCacheDAO {

    String TABLE = "`" + TABLE_PLACEHOLDER + "`";
    String FIELD = "`key`, `flags`, `data`, `expire`, `saveAt`";
    String FIELD_VERSION = "`key`, `flags`, `data`, `expire`, `version`";

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + TABLE + " WHERE `key` = :k")
    DBItem get(@ShardBy @SQLParam("k") String key);

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + TABLE + " WHERE `key` IN (:k)")
    Collection<DBItem> get(@ShardBy @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("INSERT IGNORE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int add(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("INSERT IGNORE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int[] add(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("REPLACE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int set(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("REPLACE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt)")
    int[] set(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("UPDATE " + TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt WHERE `key` = :i.key")
    int update(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("UPDATE " + TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt WHERE `key` = :i.key")
    int[] update(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("UPDATE " + TABLE + " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `version`=:i.version + 1 WHERE `key` = :i.key and `version` = :i.version")
    int cas(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("UPDATE " + TABLE + " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `version`=:i.version + 1 WHERE `key` = :i.key and `version` = :i.version")
    int cas(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("DELETE FROM " + TABLE + " WHERE `key` = :k")
    int delete(@ShardBy @SQLParam("k") String key);

    @Override
    @SQL("DELETE FROM " + TABLE + " WHERE `key` = :k")
    int[] delete(@ShardBy @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("TRUNCATE TABLE " + TABLE)
    void flushAll(@ShardBy @SQLParam("hash") Object hash);

    @Override
    @SQL("SELECT `key` FROM " + TABLE)
    List<String> getAllKeys(@ShardBy @SQLParam("hash") Object hash);

    @Override
    @SQL("SELECT `key` FROM " + TABLE + " WHERE `UID` = :uid")
    List<String> getKeys(@SQLParam("uid") long uid, @ShardBy @SQLParam("hash") Object hash);

}
