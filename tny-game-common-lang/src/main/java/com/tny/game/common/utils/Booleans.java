package com.tny.game.common.utils;

/**
 * <p>
 *
 * @author kgtny
 * @date 2024/1/6 21:08
 **/
public final class Booleans {

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public boolean isFalse(Boolean value) {
        return value == null || !value;
    }
}
