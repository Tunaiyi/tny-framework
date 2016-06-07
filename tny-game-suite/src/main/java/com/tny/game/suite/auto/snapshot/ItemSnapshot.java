package com.tny.game.suite.auto.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.SnapshotType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "@T")
public abstract class ItemSnapshot implements Snapshot {

    @JsonProperty(index = 2)
    protected int iid;

    @JsonProperty(index = 1)
    protected Long id = null;

    protected long pid;

    protected SnapshotType type;

    protected ItemSnapshot(SnapshotType type) {
        this.type = type;
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

    @Override
    public SnapshotType getType() {
        return this.type;
    }

    protected void setIDs(int itemID, long id) {
        this.iid = itemID;
        this.id = itemID == id ? null : id;
    }

    protected void setPid(long pid) {
        this.pid = pid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        ItemSnapshot other = (ItemSnapshot) obj;
        if (this.id != other.id)
            return false;
        if (this.iid != other.iid)
            return false;
        if (this.pid != other.pid)
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        } else if (!this.type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        result = prime * result + this.iid;
        result = prime * result + (int) (this.pid ^ (this.pid >>> 32));
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }

}
