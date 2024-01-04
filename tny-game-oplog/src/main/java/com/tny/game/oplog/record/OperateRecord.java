/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.record;

import com.tny.game.oplog.*;

import java.util.*;

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

    public int getActionId() {
        return this.actionLog.getActionId();
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
        return "OperateLogDTO [uid=" + this.getUserId() + ", name=" + this.getName() + ", acid=" + this.getActionId() + ", sid=" +
               this.getServerId() + ", at=" + this.getLogAt() + ", op=" + this.getOperation()
               + ", level=" + this.getLevel()
               + ", revs=" + this.getReceiveLog() + ", coss=" + this.getConsumeLogs() + ", snaps=" + this.getSnapshots() + "]";
    }

    public List<Snapshot> getSnapshotsByType(SnapshotType type) {
        List<Snapshot> snapshots = new ArrayList<>();
        for (Snapshot snapshot : this.getSnapshots()) {
            if (snapshot.getType() == type) {
                snapshots.add(snapshot);
            }
        }
        return snapshots;
    }

}
