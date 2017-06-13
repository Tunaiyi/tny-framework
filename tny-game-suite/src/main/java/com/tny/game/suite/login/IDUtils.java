package com.tny.game.suite.login;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.tny.game.suite.core.GameInfo;


public interface IDUtils {

    long SYSTEM_PLAYER_ID = 0;

    long PLAYER_ID_OFFSET = 10000000;

    static boolean isSystem(long id) {
        return id < PLAYER_ID_OFFSET;
    }

    static boolean isPlayer(long id) {
        return id > PLAYER_ID_OFFSET;
    }

    static Range<Long> createUIDRange(int serverID) {
        return Range.range(serverID * PLAYER_ID_OFFSET, BoundType.CLOSED, serverID * PLAYER_ID_OFFSET + PLAYER_ID_OFFSET - 1, BoundType.CLOSED);
    }

    static long createUserID(int serverID, long index) {
        return serverID * PLAYER_ID_OFFSET + index;
    }

    static int userID2SID(long playerID) {
        if (playerID <= 0)
            return GameInfo.getMainServerID();
        return (int) (playerID / PLAYER_ID_OFFSET);
    }

    static boolean isOwnUser(long userID) {
        int serverID = IDUtils.userID2SID(userID);
        return GameInfo.getInfo(serverID) != null;
    }

    static long getSystemID() {
        return GameInfo.getMainServerID();
    }

}
