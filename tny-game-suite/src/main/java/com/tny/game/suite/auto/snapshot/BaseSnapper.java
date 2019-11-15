package com.tny.game.suite.auto.snapshot;

import com.tny.game.oplog.*;


public abstract class BaseSnapper<O, S extends Snapshot> implements Snapper<O, S> {

    protected SnapperType snapperType;

    protected BaseSnapper(SnapperType snapperType) {
        this.snapperType = snapperType;
    }

    @Override
    public S toSnapshot(O object) {
        S snapshot = this.snapperType.getSnapshotType().newSnapshot();
        if (snapshot != null) {
            this.setSnapshot(snapshot, object);
        }
        return snapshot;
    }

    @Override
    public SnapperType getSnapperType() {
        return this.snapperType;
    }

    public abstract void setSnapshot(S snapshot, O object);

}
