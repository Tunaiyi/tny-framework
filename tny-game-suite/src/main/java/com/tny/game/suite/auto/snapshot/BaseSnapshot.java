package com.tny.game.suite.auto.snapshot;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.tny.game.oplog.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "@T")
public abstract class BaseSnapshot implements Snapshot {

    @JsonProperty(index = 1)
    protected Long id = null;

    protected SnapshotType type;

    protected BaseSnapshot(SnapshotType type) {
        this.type = type;
    }

    @Override
    public SnapshotType getType() {
        return this.type;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Snapshot other = (Snapshot) obj;
        if (this.getId() != other.getId())
            return false;
        if (this.type == null) {
            if (other.getType() != null)
                return false;
        } else if (!this.type.equals(other.getType()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }

}
