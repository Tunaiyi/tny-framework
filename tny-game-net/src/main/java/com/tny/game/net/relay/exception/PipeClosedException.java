package com.tny.game.net.relay.exception;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 4:14 下午
 */
public class PipeClosedException extends Exception {

    public PipeClosedException() {
    }

    public PipeClosedException(String message) {
        super(message);
    }

    public PipeClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipeClosedException(Throwable cause) {
        super(cause);
    }

    public PipeClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
