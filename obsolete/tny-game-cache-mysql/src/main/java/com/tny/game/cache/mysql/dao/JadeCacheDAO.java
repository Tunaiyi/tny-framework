/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.*;
import net.paoding.rose.jade.annotation.*;

import java.util.*;

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
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FIELD +
         ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i.number) ON DUPLICATE KEY UPDATE " +
         "`flags`=VALUES(`flags`), `data`=VALUES(`data`), `expire`=VALUES(`expire`), `saveAt`=VALUES(`expire`), `uid`=VALUES(`uid`), " +
         "`itemId`=VALUES(`itemId`), `number`=VALUES(`number`)")
        // @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FULL_FIELD + ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i
        // .itemId, :i.number) ON DUPLICATE KEY UPDATE " +
        //         "`flags`=:i.flags, `data`=:i.data, `expire`=:i.expire, `saveAt`=:i.saveAt, `uid`=:i.uid, `itemId`=:i.itemId, `number`=:i.number")
    int set(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("INSERT IGNORE INTO " + ITEM_TABLE + " (" + FIELD +
         ") VALUES (:i.key, :i.flags, :i.data, :i.expire, :i.saveAt, :i.uid, :i.itemId, :i.number) ON DUPLICATE KEY UPDATE " +
         "`flags`=VALUES(`flags`), `data`=VALUES(`data`), `expire`=VALUES(`expire`), `saveAt`=VALUES(`expire`), `uid`=VALUES(`uid`), " +
         "`itemId`=VALUES(`itemId`), `number`=VALUES(`number`)")
    int[] set(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("UPDATE " + ITEM_TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt WHERE `key` = :i.key")
    int update(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("UPDATE " + ITEM_TABLE + " SET  `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt WHERE `key` = :i.key")
    int[] update(@ShardBy @SQLParam("i") Collection<? extends DBCacheItem> items);

    @Override
    @SQL("UPDATE " + ITEM_TABLE +
         " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `version`=:i.version + 1 WHERE `key` = :i.key and " +
         "`version` = :i.version")
    int cas(@SQLParam("i") DBCacheItem item);

    @Override
    @SQL("UPDATE " + ITEM_TABLE +
         " SET `data`=:i.data, `flags`=:i.flags, `expire`=:i.expire, `saveAt`=:i.saveAt, `version`=:i.version + 1 WHERE `key` = :i.key and " +
         "`version` = :i.version")
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
