/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
