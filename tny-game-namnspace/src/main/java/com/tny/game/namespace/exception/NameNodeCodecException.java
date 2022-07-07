package com.tny.game.namespace.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class NameNodeCodecException extends RuntimeException {

    public NameNodeCodecException() {

    }

    public NameNodeCodecException(String message) {
        super(message);
    }

    public NameNodeCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameNodeCodecException(Throwable cause) {
        super(cause);
    }

    public NameNodeCodecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
