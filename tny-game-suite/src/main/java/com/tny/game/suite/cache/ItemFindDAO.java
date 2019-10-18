package com.tny.game.suite.cache;

import com.tny.game.cache.mysql.DBCacheItem;
import com.tny.game.cache.mysql.dao.ShardCacheDAO;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.annotation.ShardBy;

import java.util.List;
import java.util.Map;

@DAO
public interface ItemFindDAO extends ShardCacheDAO {

    String TABLE = "`##(:table)`";
    String FIND_FIELD = "`flags`, `data`";

    //=======================================================================================================
    //=================================================FIND==================================================
    //=======================================================================================================

    @SQL("SELECT MAX(`uid`) as `max`, MIN(`uid`) as `min` FROM " + TABLE)
    UidRange getUidRange(@ShardBy @SQLParam("table") String table);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE)
    List<DBCacheItem> findAll(@ShardBy @SQLParam("table") String table);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `itemId` = :itemId")
    List<DBCacheItem> findByItemId(@ShardBy @SQLParam("table") String table, @SQLParam("itemId") int itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `itemId` in (:itemId)")
    List<DBCacheItem> findByItemId(@ShardBy @SQLParam("table") String table, @SQLParam("itemId") List<Integer> itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` = :uid")
    List<DBCacheItem> findByUid(@ShardBy @SQLParam("table") String table, @SQLParam("uid") long uid);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` = :uid and `itemId` = :itemId")
    List<DBCacheItem> findByUid(@ShardBy @SQLParam("table") String table, @SQLParam("uid") long uid, @SQLParam("itemId") int itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE uid` = :uid and `itemId` in (:itemId)")
    List<DBCacheItem> findByUid(@ShardBy @SQLParam("table") String table, @SQLParam("uid") long uid, @SQLParam("itemId") List<Integer> itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` in (:uid)")
    List<DBCacheItem> findByUids(@ShardBy @SQLParam("table") String table, @SQLParam("uid") List<Long> uids);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` in (:uid) and `itemId` = (:itemId)")
    List<DBCacheItem> findByUids(@ShardBy @SQLParam("table") String table, @SQLParam("uid") List<Long> uids, @SQLParam("itemId") int itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` in (:uid) and `itemId` in (:itemId)")
    List<DBCacheItem> findByUids(@ShardBy @SQLParam("table") String table, @SQLParam("uid") List<Long> uids, @SQLParam("itemId") List<Integer> itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` between :suid and :euid")
    List<DBCacheItem> findByUidRange(@ShardBy @SQLParam("table") String table, @SQLParam("suid") long startuid, @SQLParam("euid") long enduid);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` between :suid and :euid and `itemId` = :itemId")
    List<DBCacheItem> findByUidRange(@ShardBy @SQLParam("table") String table, @SQLParam("suid") long startuid, @SQLParam("euid") long enduid, @SQLParam("itemId") int itemId);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `uid` between :suid and :euid and `itemId` in (:itemId)")
    List<DBCacheItem> findByUidRange(@ShardBy @SQLParam("table") String table, @SQLParam("suid") long startuid, @SQLParam("euid") long enduid, @SQLParam("itemId") List<Integer> itemId);

    //==========================================================================================
    //##########################################################################################
    //==========================================================================================

    @SQL("select `itemId`, sum(`number`) from " + TABLE + " group by `itemId` order by null")
    Map<Integer, Long> countItemNumber(@ShardBy @SQLParam("table") String table);

}
