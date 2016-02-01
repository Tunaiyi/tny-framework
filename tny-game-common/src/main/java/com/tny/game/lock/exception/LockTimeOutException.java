package com.tny.game.lock.exception;

/**
 * 当锁在release的状态下,会抛出改异常
 *
 * @author KGTny
 */
public class LockTimeOutException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -7388196765155100590L;

    public LockTimeOutException() {
        super();
    }

    public LockTimeOutException(String message) {
        super(message);
    }

    public LockTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockTimeOutException(Throwable cause) {
        super(cause);
    }

}
