package com.tny.game.oplog;

public interface SnapshotType {

    int getID();

    <S extends Snapshot> S newSnapshot();

}
