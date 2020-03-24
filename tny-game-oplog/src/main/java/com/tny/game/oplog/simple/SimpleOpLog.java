package com.tny.game.oplog.simple;

import com.tny.game.oplog.*;

import java.time.Instant;
import java.util.*;

public class SimpleOpLog extends OpLog {

    private Object protocol;

    private Instant createAt;

    private String threadName;

    private List<UserOpLog> userLogs = new ArrayList<>();

    public SimpleOpLog(Object protocol) {
        super();
        this.protocol = protocol;
        this.createAt = Instant.now();
        this.threadName = Thread.currentThread().getName();
    }

    @Override
    protected UserOpLog putUserOpLog(UserOpLog userOpLog) {
        UserOpLog old = this.getBaseUserOpLog(userOpLog.getUserId());
        if (old != null) {
            return old;
        }
        this.userLogs.add(userOpLog);
        return userOpLog;
    }

    @Override
    public UserOpLog getUserOpLog(long userID) {
        return this.getBaseUserOpLog(userID);
    }

    private UserOpLog getBaseUserOpLog(long userID) {
        for (int index = 0; index < this.userLogs.size(); index++) {
            UserOpLog log = this.userLogs.get(index);
            if (log.getUserId() == userID)
                return log;
        }
        return null;
    }

    @Override
    public Object getProtocol() {
        return this.protocol;
    }

    @Override
    public Instant getCreateAt() {
        return this.createAt;
    }

    @Override
    public String getThreadName() {
        return this.threadName;
    }

    //	@Override
    //	public int getServerID() {
    //		return this.serverID;
    //	}

    @Override
    public List<UserOpLog> getUserLogs() {
        List<? extends UserOpLog> list = this.userLogs;
        return Collections.unmodifiableList(list);
    }

    //	@Override
    //	protected BaseUserOpLog getBaseUserOpLog(long userID) {
    //		return this.userOpLogMap.get(userID);
    //	}

}
