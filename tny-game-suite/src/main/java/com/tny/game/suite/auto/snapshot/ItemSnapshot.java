package com.tny.game.suite.auto.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.SnapshotType;

public abstract class ItemSnapshot extends BaseSnapshot {

    @JsonProperty(index = 2)
    protected int iid;

    protected long pid;

    protected ItemSnapshot(SnapshotType type) {
        super(type);
    }

    @Override
    public long getID() {
        return this.id == null ? this.iid : this.id;
    }

    public int getItemID() {
        return this.iid;
    }

    @Override
    public long getPlayerID() {
        return this.pid;
    }

    protected void setIDs(int itemID, long id) {
        this.iid = itemID;
        this.id = itemID == id ? null : id;
    }

    protected void setPid(long pid) {
        this.pid = pid;
    }

}
