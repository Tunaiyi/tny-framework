package com.tny.game.oplog;

public interface SnapshotType {

    <S extends Snapshot> S newSnapshot();

}
