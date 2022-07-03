package com.tny.game.namespace.etcd.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class NameNodeException extends RuntimeException {

    public NameNodeException() {

    }

    public NameNodeException(String message) {
        super(message);
    }

    public NameNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameNodeException(Throwable cause) {
        super(cause);
    }

    public NameNodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
