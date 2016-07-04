package com.tny.game.suite.doors.dao;

import java.util.Collection;
import java.util.List;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

@DAO
public interface PlatformDAO {

	public static final String TABLE = "`Platform`";
	public static final String FIELDS = "`id`, `mark`, `name`, `zoneID`";

	@SQL("select " + FIELDS + " from " + TABLE)
	public List<PlatformVO> getAll();

	@SQL("replace into " + TABLE + "(" + FIELDS + ") values(:pf.id,:pf.mark,:pf.name,:pf.zoneID)")
	public int save(@SQLParam("pf") PlatformVO platforms);

	@SQL("replace into " + TABLE + "(" + FIELDS + ") values(:pf.id,:pf.mark,:pf.name,:pf.zoneID)")
	public int save(@SQLParam("pf") Collection<PlatformVO> zone);

	@SQL("delete from " + TABLE + " where `id` = :id")
	public int delect(@SQLParam("id") int id);

}