package com.tny.game.protoex.exception;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-02-09 00:51
 */
public class UnsafeInitException extends RuntimeException {

    public UnsafeInitException() {
    }

    public UnsafeInitException(String message) {
        super(message);
    }

    public UnsafeInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsafeInitException(Throwable cause) {
        super(cause);
    }

    public UnsafeInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
