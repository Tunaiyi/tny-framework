package com.tny.game.oplog.record;

import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.StuffSettleLog;
import com.tny.game.oplog.UserOpLog;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class UserStuffRecord extends AbstractLog {

    /**
     * 日志类型
     */
    public static final String TYPE = "stuff_flow";

    private List<StuffSettleLog> stuffLogs;

    @SuppressWarnings("unchecked")
    public UserStuffRecord(String logID, OpLog log, UserOpLog userOpLog) {
        super(TYPE, logID, log, userOpLog);
    }

    public Collection<StuffSettleLog> getStuffLogs() {
        return userOpLog.getStuffSettleLogs();
    }

    public DateTime getCreateAt() {
        return userOpLog.getCreateAt();
    }

}
