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
