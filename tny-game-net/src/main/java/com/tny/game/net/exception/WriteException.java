package com.tny.game.net.exception;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 14:48
 */
public class WriteException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public WriteException() {
    }

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteException(Throwable cause) {
        super(cause);
    }

    public WriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
