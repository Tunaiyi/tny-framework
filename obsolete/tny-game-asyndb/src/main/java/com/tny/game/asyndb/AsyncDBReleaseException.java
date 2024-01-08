package com.tny.game.asyndb;

/**
 * 持久化对象被释放异常
 *
 * @author KGTny
 */
public class AsyncDBReleaseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -4615013858237025617L;

    public AsyncDBReleaseException(String msg) {
        super(msg);
    }

}
