package com.tny.game.net.relay.exception;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 4:14 下午
 */
public class InvokeHandlerException extends Exception {

    public InvokeHandlerException() {
    }

    public InvokeHandlerException(String message) {
        super(message);
    }

    public InvokeHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeHandlerException(Throwable cause) {
        super(cause);
    }

    public InvokeHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
