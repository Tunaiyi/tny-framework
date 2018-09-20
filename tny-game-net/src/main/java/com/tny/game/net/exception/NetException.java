package com.tny.game.net.exception;

public class NetException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NetException() {
    }

    public NetException(String message) {
        super(message);
    }

    public NetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetException(Throwable cause) {
        super(cause);
    }

    public NetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
