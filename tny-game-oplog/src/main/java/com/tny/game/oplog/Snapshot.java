package com.tny.game.oplog;

public interface Snapshot {

    /**
     * Snapshot Type ID;
     *
     * @return
     */
    SnapshotType getType();

    /**
     * ID
     *
     * @return
     */
    long getID();

    /**
     * PlayerID
     *
     * @return
     */
    long getPlayerID();

}
