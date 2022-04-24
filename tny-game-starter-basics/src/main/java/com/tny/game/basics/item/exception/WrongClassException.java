package com.tny.game.basics.item.exception;

/**
 * Created by Kun Yang on 2016/10/26.
 */
public class WrongClassException extends RuntimeException {

    public WrongClassException() {
    }

    public WrongClassException(String message) {
        super(message);
    }

    public WrongClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongClassException(Throwable cause) {
        super(cause);
    }

    public WrongClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
