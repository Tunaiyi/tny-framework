package com.tny.game.suite.auto.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.basics.item.*;
import com.tny.game.oplog.*;

public abstract class ItemSnapshot extends BaseSnapshot {

    @JsonProperty(index = 2)
    protected int iid;

    protected long pid;

    protected ItemSnapshot(SnapshotType type) {
        super(type);
    }

    @Override
    public long getId() {
        return this.id == null ? this.iid : this.id;
    }

    public int getItemId() {
        return this.iid;
    }

    @Override
    public long getPlayerId() {
        return this.pid;
    }

    public void setItem(Item<?> item) {
        this.setIDs(item.getItemId(), item.getId());
        this.setPid(item.getPlayerId());
    }

    public void setIDs(int itemId, long id) {
        this.iid = itemId;
        this.id = itemId == id ? null : id;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

}
