package com.tny.game.namespace.etcd.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class LesseeException extends RuntimeException {

    public LesseeException() {

    }

    public LesseeException(String message) {
        super(message);
    }

    public LesseeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LesseeException(Throwable cause) {
        super(cause);
    }

    public LesseeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
