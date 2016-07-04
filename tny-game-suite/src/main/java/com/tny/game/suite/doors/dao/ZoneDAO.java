package com.tny.game.suite.doors.dao;

import java.util.Collection;
import java.util.List;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

@DAO
public interface ZoneDAO {

	public static final String TABLE = "`Zone`";
	public static final String FIELDS = "`zoneID`, `name`, `version`";

	@SQL("select " + FIELDS + " from " + TABLE)
	public List<ZoneVO> getAll();

	@SQL("insert into " + TABLE + "(" + FIELDS + ") values(:z.zoneID,:z.name,:z.version)")
	public int insert(@SQLParam("z") ZoneVO zone);

	@SQL("replace into " + TABLE + "(" + FIELDS + ") values(:z.zoneID,:z.name,:z.version)")
	public int save(@SQLParam("z") ZoneVO zone);

	@SQL("replace into " + TABLE + "(" + FIELDS + ") values(:z.zoneID,:z.name,:z.version)")
	public int save(@SQLParam("z") Collection<ZoneVO> zone);

	@SQL("delete from " + TABLE + " where `zoneID` = :zoneID")
	public int delect(@SQLParam("zoneID") int zoneID);

}
