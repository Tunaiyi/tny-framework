package com.tny.game.net.netty4.appliaction.exception;

/**
 * Created by Kun Yang on 16/1/30.
 */
public class LifecycleProcessException extends RuntimeException {

    public LifecycleProcessException(Throwable cause) {
        super(cause);
    }

    public LifecycleProcessException(String message) {
        super(message);
    }

    public LifecycleProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public LifecycleProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
