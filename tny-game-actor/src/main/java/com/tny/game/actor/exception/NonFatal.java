package com.tny.game.actor.exception;

/**
 * 非致命判断
 * Created by Kun Yang on 16/1/18.
 */
public final class NonFatal {

    private NonFatal() {
    }

    /**
     * 判断指定的异常e是否为非致命的异常
     * @param e 指定异常
     * @return 是否为非致命的异常
     */
    public static boolean isNonFatal(Throwable e) {
        return StackOverflowError.class.isInstance(e)
                || !(e instanceof Error && (VirtualMachineError.class.isInstance(e)
                || ThreadDeath.class.isInstance(e)
                || InterruptedException.class.isInstance(e)
                || LinkageError.class.isInstance(e)));
    }

}
