package com.tny.game.namespace.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class NameNodeWatchException extends RuntimeException {

    public NameNodeWatchException() {

    }

    public NameNodeWatchException(String message) {
        super(message);
    }

    public NameNodeWatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameNodeWatchException(Throwable cause) {
        super(cause);
    }

    public NameNodeWatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
