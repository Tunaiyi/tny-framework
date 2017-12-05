package com.tny.game.oplog.record;

import com.tny.game.suite.base.DateTimeAide;
import com.tny.game.oplog.Log;
import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.UserOpLog;
import org.joda.time.DateTime;

/**
 *
 */
public class AbstractLog implements Log {

    /**
     * 日志ID
     */
    private String logID;
    /**
     * 日志
     */
    protected OpLog log;
    /**
     * 玩家日志
     */
    protected UserOpLog userOpLog;

    /**
     * 日志类型
     */
    private String type;

    /**
     * 创建日期
     */
    private int date;

    @SuppressWarnings("unchecked")
    public AbstractLog(String type, String logID, OpLog log, UserOpLog userOpLog) {
        this.type = type;
        this.logID = logID;
        this.log = log;
        this.userOpLog = userOpLog;
        DateTime dateTime = log.getCreateAt();
        this.date = DateTimeAide.date2Int(dateTime);
    }

    @Override
    public long getUserID() {
        return this.userOpLog.getUserID();
    }

    @Override
    public String getName() {
        return this.userOpLog.getName();
    }

    @Override
    public long getAt() {
        return this.getLogAt().getMillis();
    }

    @Override
    public int getDate() {
        return this.date;
    }

    @Override
    public int getServerID() {
        return this.userOpLog.getServerID();
    }

    @Override
    public String getPF() {
        return userOpLog.getPF();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getLevel() {
        return this.userOpLog.getLevel();
    }

    @Override
    public int getVip() {
        return this.userOpLog.getVip();
    }

    @Override
    public String getOpenID() {
        return this.userOpLog.getOpenID();
    }

    @Override
    public DateTime getLogAt() {
        return this.log.getCreateAt();
    }

    @Override
    public String getLogID() {
        return logID;
    }

}
