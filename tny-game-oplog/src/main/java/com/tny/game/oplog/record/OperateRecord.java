package com.tny.game.oplog.record;

import com.tny.game.oplog.ActionLog;
import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.SnapshotType;
import com.tny.game.oplog.StuffTradeLog;
import com.tny.game.oplog.UserOpLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class OperateRecord extends AbstractLog {

    /**
     * 日志类型
     */
    public static final String TYPE = "oplog";

    /**
     * 玩家操作日志
     */
    protected ActionLog actionLog;

    @SuppressWarnings("unchecked")
    public OperateRecord(String logID, OpLog log, UserOpLog userOpLog, ActionLog actionLog) {
        super(TYPE, logID, log, userOpLog);
        this.actionLog = actionLog;
    }

    public int getActionID() {
        return this.actionLog.getActionID();
    }

    public Object getOperation() {
        return this.log.getProtocol();
    }

    public Collection<StuffTradeLog> getReceiveLog() {
        return this.actionLog.getReceiveLogs();
    }

    public Collection<StuffTradeLog> getConsumeLogs() {
        return this.actionLog.getConsumeLogs();
    }

    public Collection<Snapshot> getSnapshots() {
        return this.actionLog.getSnapshots();
    }

    @Override
    public String toString() {
        return "OperateLogDTO [uid=" + this.getUserID() + ", name=" + this.getName() + ", acid=" + this.getActionID() + ", sid=" + this.getServerID() + ", at=" + this.getLogAt() + ", op=" + this.getOperation()
                + ", level=" + this.getLevel()
                + ", revs=" + this.getReceiveLog() + ", coss=" + this.getConsumeLogs() + ", snaps=" + this.getSnapshots() + "]";
    }

    public List<Snapshot> getSnapshotsByType(SnapshotType type) {
        List<Snapshot> snapshots = new ArrayList<>();
        for (Snapshot snapshot : this.getSnapshots()) {
            if (snapshot.getType() == type)
                snapshots.add(snapshot);
        }
        return snapshots;
    }

}
