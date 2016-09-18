package com.tny.game.suite.scheduler.database;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.context.annotation.Profile;

import java.sql.Blob;

import static com.tny.game.suite.SuiteProfiles.*;

@DAO
@Profile({SCHEDULER_DB})
public interface SchedulerObjectDAO {

	String BACK_UP_KEY = "backup";
	String RECEIVER_KEY = "receiver";

	String TABLE = "`SchedulerObject`";
	String FIELDS = "`key`, `data`";

	@SQL("select `data` from " + TABLE + " where `key` = :key")
	Blob get(@SQLParam("key") String key);

	@SQL("replace into " + TABLE + "(" + FIELDS + ") values(:key, :data)")
	int set(@SQLParam("key") String key, @SQLParam("data") Blob blob);

}
