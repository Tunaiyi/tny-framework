package com.tny.game.suite.auto.snapshot;

import com.tny.game.base.item.Item;
import com.tny.game.oplog.Snapper;
import com.tny.game.oplog.SnapperType;
import com.tny.game.oplog.SnapshotType;

public abstract class ItemSnapper<I extends Item<?>, S extends ItemSnapshot> implements Snapper<I, S> {

    private SnapperType snapperType;

    protected ItemSnapper(SnapperType snapperType) {
        this.snapperType = snapperType;
    }

    @Override
    public S toSnapshot(I item) {
        S snapshot = this.snapperType.getSnapshotType().newSnapshot();
        if (snapshot != null) {
            snapshot.setIDs(item.getItemID(), item.getID());
            snapshot.setPid(item.getPlayerID());
            this.setSnapshot(snapshot, item);
        }
        return snapshot;
    }

    @Override
    public long getSnapshotID(I item) {
        return item.getID();
    }

    @Override
    public SnapshotType getSnapshotType() {
        return this.snapperType.getSnapshotType();
    }

    @Override
    public SnapperType getSnapperType() {
        return this.snapperType;
    }

    public abstract void setSnapshot(S snapshot, I item);

}
