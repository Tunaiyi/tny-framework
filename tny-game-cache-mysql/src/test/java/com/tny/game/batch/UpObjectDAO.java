package com.tny.game.batch;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import java.util.Collection;

@DAO(catalog = "cacheShardStrategy")
public interface UpObjectDAO {

    public static final String TABLE = "`UpObject`";
    public static final String FIELD = "`id`, `name`, `age`, `gender`";

    @SQL("SELECT " + FIELD + " FROM " + TABLE + " where `id` in (:keys)")
    public Collection<UpObject> get(@SQLParam("keys") Collection<Integer> keys);

    @SQL("INSERT INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public int insert(@SQLParam("i") UpObject object);

    @SQL("INSERT IGNORE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public boolean insertIgnore(@SQLParam("i") UpObject object);

    @SQL("INSERT INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public int[] insert(@SQLParam("i") Collection<UpObject> objects);

    @SQL("REPLACE INTO " + TABLE + " (" + FIELD + ") VALUES (:i.id, :i.name, :i.age, :i.gender)")
    public int[] set(@SQLParam("i") Collection<UpObject> objects);

    @SQL("UPDATE " + TABLE + " SET `name`=:i.name, `age`=:i.age, `gender`=:i.gender where `id` = :i.id")
    public int update(@SQLParam("i") Collection<UpObject> item);

    @SQL("TRUNCATE TABLE " + TABLE + ";")
    public void flushAll();

}
