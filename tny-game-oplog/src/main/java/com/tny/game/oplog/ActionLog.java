/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog;

import java.util.Collection;

public abstract class ActionLog {

    /**
     * 行为 ID
     *
     * @return
     */
    public abstract int getBehaviorId();

    /**
     * 动作 ID
     *
     * @return
     */
    public abstract int getActionId();

    /**
     * 交易日志
     *
     * @return
     */
    public abstract Collection<StuffTradeLog> getReceiveLogs();

    /**
     * 交易日志
     *
     * @return
     */
    public abstract Collection<StuffTradeLog> getConsumeLogs();

    /**
     * 快照
     *
     * @return
     */
    public abstract Collection<Snapshot> getSnapshots();

    protected abstract ActionLog logReceive(long id, int itemId, long oldNum, long alter, long newNum);

    protected abstract ActionLog logConsume(long id, int itemId, long oldNum, long alter, long newNum);

    protected abstract ActionLog logSnapshot(Snapshot snapshots);

    protected abstract Snapshot getSnapshot(long id, SnapshotType type);

}
