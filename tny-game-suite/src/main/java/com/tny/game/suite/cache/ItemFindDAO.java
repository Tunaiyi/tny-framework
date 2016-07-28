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

    String TABLE_PLACEHOLDER = "$T";
    String TABLE = "`" + TABLE_PLACEHOLDER + "`";
    String FIND_FIELD = "`flags`, `data`";

    @SQL("select `playerID` from `Player` where `name` = :name limit 1")
    Long getUIDByName(@SQLParam("name") String name, @ShardBy @SQLParam("table") ShardTable table);

    //=======================================================================================================
    //=================================================FIND==================================================
    //=======================================================================================================

    @SQL("SELECT MAX(`UID`) as `max`, MIN(`UID`) as `min` FROM " + TABLE)
    UIDRange getUIDRange(@ShardBy @SQLParam("table") ShardTable table);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE)
    List<DBCacheItem> findAll(@ShardBy @SQLParam("table") ShardTable table);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `itemID` = :itemID")
    List<DBCacheItem> findByItemID(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("itemID") int itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `itemID` in (:itemID)")
    List<DBCacheItem> findByItemID(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("itemID") List<Integer> itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` = :UID")
    List<DBCacheItem> findByUID(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("UID") long UID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` = :UID and `itemID` = :itemID")
    List<DBCacheItem> findByUID(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("UID") long UID, @SQLParam("itemID") int itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE UID` = :UID and `itemID` in (:itemID)")
    List<DBCacheItem> findByUID(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("UID") long UID, @SQLParam("itemID") List<Integer> itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` in (:UID)")
    List<DBCacheItem> findByUIDs(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("UID") List<Long> UIDs);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` in (:UID) and `itemID` = (:itemID)")
    List<DBCacheItem> findByUIDs(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("UID") List<Long> UIDs, @SQLParam("itemID") int itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` in (:UID) and `itemID` in (:itemID)")
    List<DBCacheItem> findByUIDs(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("UID") List<Long> UIDs, @SQLParam("itemID") List<Integer> itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` between :sUID and :eUID")
    List<DBCacheItem> findByUIDRange(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("sUID") long startUID, @SQLParam("eUID") long endUID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` between :sUID and :eUID and `itemID` = :itemID")
    List<DBCacheItem> findByUIDRange(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("sUID") long startUID, @SQLParam("eUID") long endUID, @SQLParam("itemID") int itemID);

    @SQL("SELECT " + FIND_FIELD + " FROM " + TABLE + " WHERE `UID` between :sUID and :eUID and `itemID` in (:itemID)")
    List<DBCacheItem> findByUIDRange(@ShardBy @SQLParam("table") ShardTable table, @SQLParam("sUID") long startUID, @SQLParam("eUID") long endUID, @SQLParam("itemID") List<Integer> itemID);

    //==========================================================================================
    //##########################################################################################
    //==========================================================================================

    @SQL("select `itemID`, sum(`number`) from " + TABLE + " group by `itemID` order by null")
    Map<Integer, Long> countItemNumber(@ShardBy @SQLParam("table") ShardTable table);

}
