package com.tny.game.suite.cache;

import com.tny.game.cache.mysql.*;
import com.tny.game.cache.mysql.dao.*;
import net.paoding.rose.jade.annotation.*;

import java.util.*;

@DAO
public interface GameCacheDAO extends CacheDAO, ShardCacheDAO {

    String SEPARATOR = ":";
    String KEY_TABLE = "`##(:k.substring(0, :k.indexOf($SEPARATOR)))`";
    String ITEM_TABLE = "`##(:i.key.substring(0, :i.key.indexOf($SEPARATOR)))`";
    String KEYS_TABLE = "`##(:key.substring(0, :key.indexOf($SEPARATOR)))`";
    String ITEMS_TABLE = "`##(:item.key.substring(0, :item.key.indexOf($SEPARATOR)))`";
    String FIELD = "`key`, `flags`, `data`, `expire`, `saveAt`";
    String FULL_FIELD = FIELD + ", `uid`, `itemId`, `number`";
    String FIELD_VERSION = "`key`, `flags`, `data`, `expire`, `version`";

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + KEY_TABLE + " WHERE `key` = :k")
    DBCacheItem get(@SQLParam("k") String key);

    @Override
    @SQL("SELECT " + FIELD_VERSION + " FROM " + KEYS_TABLE + " WHERE `key` IN (:k)")
    Collection<DBCacheItem> get(@ShardBy("key") @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FULL_FIELD +
            ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i.number)")
    int add(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEMS_TABLE + " (" + FULL_FIELD +
            ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i.number)")
    int[] add(@ShardBy("item") @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FULL_FIELD +
            ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i.number) ON DUPLICATE KEY UPDATE " +
            "`flags`=VALUES(`flags`), `data`=VALUES(`data`), `expire`=VALUES(`expire`), `saveAt`=VALUES(`expire`), `uid`=VALUES(`uid`), " +
            "`itemId`=VALUES(`itemId`), `number`=VALUES(`number`)")
        // @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i
        // .itemId, :i.number) ON DUPLICATE KEY UPDATE " +
        //         "`flags`=:i.flags, `data`=:i.data, `expire`=:i.expire, `saveAt`=:i.saveAt, `uid`=:i.uid, `itemId`=:i.itemId, `number`=:i.number")
    int set(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FULL_FIELD +
            ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i.number) ON DUPLICATE KEY UPDATE " +
            "`flags`=VALUES(`flags`), `data`=VALUES(`data`), `expire`=VALUES(`expire`), `saveAt`=VALUES(`expire`), `uid`=VALUES(`uid`), " +
            "`itemId`=VALUES(`itemId`), `number`=VALUES(`number`)")
    int[] set(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    // @Override
    // @SQL("REPLACE INTO " + ITEM_TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i
    // .number)")
    // int set(@SQLParam("i") DBCacheItem item);
    //
    // @Override
    // @SQL("REPLACE INTO " + ITEMS_TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i
    // .number)")
    // int[] set(@ShardBy("item") @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("UPDATE " + ITEM_TABLE +
            " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number WHERE `key` = :i.key")
    int update(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("UPDATE " + ITEMS_TABLE +
            " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number  WHERE `key` = :i.key")
    int[] update(@ShardBy("item") @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("UPDATE " + ITEM_TABLE +
            " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number, `version`=:i.version + 1 WHERE " +
            "`key` = :i.key and `version` = :i.version")
    int cas(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("UPDATE " + ITEMS_TABLE +
            " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `number`=:i.number, `version`=:i.version + 1 WHERE " +
            "`key` = :i.key and `version` = :i.version")
    int cas(@ShardBy("item") @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("DELETE FROM " + KEY_TABLE + " WHERE `key` = :k")
    int delete(@SQLParam("k") String key);

    @Override
    @SQL("DELETE FROM " + KEYS_TABLE + " WHERE `key` = :k")
    int[] delete(@ShardBy("key") @SQLParam("k") Collection<String> keys);

    @Override
    @SQL("TRUNCATE TABLE ##(:hash)")
    void flushAll(@SQLParam("hash") Object hash);

    @Override
    @SQL("SELECT `key` FROM ##(:hash)")
    List<String> getAllKeys(@SQLParam("hash") Object hash);

    @Override
    @SQL("SELECT `key` FROM ##(:hash) WHERE `UID` = :uid")
    List<String> getKeys(@SQLParam("uid") long uid, @SQLParam("hash") Object hash);

}
