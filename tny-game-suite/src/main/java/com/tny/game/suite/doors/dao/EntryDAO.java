package com.tny.game.suite.doors.dao;

import java.util.Collection;
import java.util.List;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

@DAO
public interface EntryDAO {

	public static final String TABLE = "`Entry`";
	public static final String FIELDS = "`number`, `zoneID`, `serverID`, `name`, `onlineType`, `entryState`";

	@SQL("select " + FIELDS + " from " + TABLE + " where `zoneID` = :zoneID")
	public List<EntryVO> getByZoneID(@SQLParam("zoneID") int zoneID);

	@SQL("replace into " + TABLE + "(" + FIELDS + ") values(:entry.number,:entry.zoneID,:entry.serverID,:entry.name,:entry.onlineType,:entry.entryState)")
	public int save(@SQLParam("entry") Collection<EntryVO> zone);

	@SQL("delete from " + TABLE + " where `zoneID` = :zoneID and `number` in (:snum)")
	public int delect(@SQLParam("zoneID") int zoneID, @SQLParam("snum") Collection<Integer> number);

}