package com.tny.game.common.concurrent;

/**
 * Created by Kun Yang on 2016/12/16.
 */
public class ExeUncheckedException extends RuntimeException {

    public ExeUncheckedException() {
    }

    public ExeUncheckedException(String message) {
        super(message);
    }

    public ExeUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExeUncheckedException(Throwable cause) {
        super(cause);
    }

    public ExeUncheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
