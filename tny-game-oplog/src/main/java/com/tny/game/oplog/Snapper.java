package com.tny.game.oplog;

public interface Snapper<O, S extends Snapshot> {

    S toSnapshot(O o);

    SnapshotType getSnapshotType();

    SnapperType getSnapperType();

    long getSnapshotID(O object);

    void update(S snapshot, O object);

}
