package com.tny.game.suite.cache;

import com.tny.game.cache.mysql.DBItem;
import com.tny.game.cache.mysql.dao.CacheDAO;
import com.tny.game.cache.mysql.dao.ShardCacheDAO;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import java.util.Collection;

@DAO
public interface GameCacheDAO extends CacheDAO, ShardCacheDAO {

    String TABLE_PLACEHOLDER = "$T";
    String TABLE = "`" + TABLE_PLACEHOLDER + "`";
    String FIELD = "`key`, `flags`, `data`, `expire`, `saveAt`";
    String FULL_FIELD = FIELD + ", `uid`, `itemID`, `number`";
    String FIELD_VERSION = "`key`, `flags`, `data`, `expire`, `version`";

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + TABLE + " WHERE `key` = :k")
    DBItem get(@ShardBy @SQLParam("k") String key);

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + TABLE + " WHERE `key` IN (:k)")
    Collection<DBItem> get(@ShardBy @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("INSERT IGNORE INTO " + TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemID, :i.number)")
    int add(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("INSERT IGNORE INTO " + TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemID, :i.number)")
    int[] add(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("REPLACE INTO " + TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemID, :i.number)")
    int set(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("REPLACE INTO " + TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemID, :i.number)")
    int[] set(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("UPDATE " + TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number WHERE `key` = :i.key")
    int update(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("UPDATE " + TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number  WHERE `key` = :i.key")
    int[] update(@ShardBy("key") @SQLParam("i") Collection<? extends DBItem> items);

    @Override
    @SQL("UPDATE " + TABLE + " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number, `version`=:i.version + 1 WHERE `key` = :i.key and `version` = :i.version")
    int cas(@ShardBy("key") @SQLParam("i") DBItem item);

    @Override
    @SQL("UPDATE " + TABLE + " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number, `version`=:i.version + 1 WHERE `key` = :i.key and `version` = :i.version")
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

}
