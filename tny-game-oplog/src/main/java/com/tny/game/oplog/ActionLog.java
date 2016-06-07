package com.tny.game.oplog;

import java.util.Collection;

public abstract class ActionLog {

    /**
     * 行为 ID
     *
     * @return
     */
    public abstract int getBehaviorID();

    /**
     * 动作 ID
     *
     * @return
     */
    public abstract int getActionID();

    /**
     * 交易日志
     *
     * @return
     */
    public abstract Collection<TradeLog> getReceiveLogs();

    /**
     * 交易日志
     *
     * @return
     */
    public abstract Collection<TradeLog> getConsumeLogs();

    /**
     * 快照
     *
     * @return
     */
    public abstract Collection<Snapshot> getSnapshots();

    protected abstract ActionLog logReceive(long id, int itemID, long oldNum, long alter, long newNum);

    protected abstract ActionLog logConsume(long id, int itemID, long oldNum, long alter, long newNum);

    protected abstract ActionLog logSnapshot(Snapshot snapshots);

    protected abstract Snapshot getSnapshot(long id, SnapshotType type);

}
