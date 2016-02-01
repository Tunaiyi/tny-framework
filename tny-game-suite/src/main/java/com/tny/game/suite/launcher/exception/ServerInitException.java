package com.tny.game.suite.launcher.exception;

/**
 * Created by Kun Yang on 16/1/30.
 */
public class ServerInitException extends Exception {

    ServerInitException(Throwable cause) {
        super(cause);
    }

    public ServerInitException(String message) {
        super(message);
    }

    public ServerInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
