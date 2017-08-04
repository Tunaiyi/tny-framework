package com.tny.game.oplog;

public interface Snapper<O, S extends Snapshot> {

    S toSnapshot(O o);

    SnapperType getSnapperType();

    default SnapshotType getSnapshotType() {
        return getSnapperType().getSnapshotType();
    }

    long getSnapshotID(O object);

    void update(S snapshot, O object);

}
