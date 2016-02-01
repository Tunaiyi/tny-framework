package com.tny.game.suite.login;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;


public class IDUtils {

    public static final long PLAYER_ID_OFFSET = 10000000;

    public static boolean isSystem(long id) {
        return id < PLAYER_ID_OFFSET;
    }

    public static Range<Long> createUIDRange(int serverID) {
        return Range.range(serverID * PLAYER_ID_OFFSET, BoundType.CLOSED, serverID * PLAYER_ID_OFFSET + PLAYER_ID_OFFSET - 1, BoundType.CLOSED);
    }

    public static long createUserID(int serverID, long index) {
        return 1L * serverID * PLAYER_ID_OFFSET + index;
    }

    public static int userID2SID(long playerID) {
        return (int) (playerID / PLAYER_ID_OFFSET);
    }

}
