package com.tny.game.suite.login;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.tny.game.suite.core.GameInfo;


public class IDAide {

    // long SYSTEM_PLAYER_ID = 0;

    public static long PLAYER_ID_OFFSET = 10000000;

    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
            'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z'};

    public static boolean isSystem(long id) {
        return id < PLAYER_ID_OFFSET;
    }

    public static boolean isPlayer(long id) {
        return id > PLAYER_ID_OFFSET;
    }

    public static Range<Long> createUIDRange(int serverID) {
        return Range.range(serverID * PLAYER_ID_OFFSET, BoundType.CLOSED, serverID * PLAYER_ID_OFFSET + PLAYER_ID_OFFSET - 1, BoundType.CLOSED);
    }

    public static long createUserID(int serverID, long index) {
        return serverID * PLAYER_ID_OFFSET + index;
    }

    public static int userID2SID(long playerID) {
        if (playerID <= 0)
            return GameInfo.getMainServerID();
        return (int) (playerID / PLAYER_ID_OFFSET);
    }

    public static boolean isOwnUser(long userID) {
        int serverID = IDAide.userID2SID(userID);
        return GameInfo.getInfo(serverID) != null;
    }

    public static long getSystemID() {
        return GameInfo.getMainServerID();
    }

    public static String numeric2String(long i, int scale) {
        long num;
        if (i < 0) {
            num = ((long) 2 * 0x7fffffff) + i + 2;
        } else {
            num = i;
        }
        char[] buf = new char[64];
        int charPos = 64;
        while ((num / scale) > 0) {
            buf[--charPos] = digits[(int) (num % scale)];
            num /= scale;
        }
        buf[--charPos] = digits[(int) (num % scale)];
        return new String(buf, charPos, (64 - charPos));
    }

    public static long string2Numeric(String s, int scale) {
        char[] buf = new char[s.length()];
        s.getChars(0, s.length(), buf, 0);
        long num = 0;
        for (int i = 0; i < buf.length; i++) {
            for (int j = 0; j < digits.length; j++) {
                if (digits[j] == buf[i]) {
                    num += j * Math.pow(scale, buf.length - i - 1);
                    break;
                }
            }
        }
        return num;
    }

    public static void main(String[] args) {
        // for (int i = 0; i < Long.MAX_VALUE; i++) {
        //     System.out.println(numeric2String(i, digits.length));
        // }
        // System.out.println(numeric2String(Integer.MAX_VALUE, 2));
        System.out.println(Long.MAX_VALUE);
        System.out.println(string2Numeric(numeric2String(Long.MAX_VALUE - 1, digits.length), digits.length));
    }

}
