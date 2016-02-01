package com.tny.game.oplog.simple;

import com.tny.game.oplog.SnapshotType;

public abstract class BaseSnapshot {

    private long ID;

    private long playerID;

    private SnapshotType snapshotType;

    protected BaseSnapshot(int playerID, long iD, SnapshotType snapshotType) {
        this.playerID = playerID;
        this.ID = iD;
        this.snapshotType = snapshotType;
    }

    public long getID() {
        return ID;
    }

    public long getPlayerID() {
        return playerID;
    }

    public SnapshotType getSnapshotType() {
        return snapshotType;
    }

}
