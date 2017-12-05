package com.tny.game.suite.base;

/**
 * 断言
 * Created by Kun Yang on 16/1/17.
 */
public class Asserts {

    public static void assertAt(boolean suspended) {
        if (suspended)
            throw new AssertionError();
    }

    public static void assertAt(boolean suspended, String message, Object... msgParams) {
        if (suspended)
            throw new AssertionError(Logs.format(message, msgParams));
    }

}
