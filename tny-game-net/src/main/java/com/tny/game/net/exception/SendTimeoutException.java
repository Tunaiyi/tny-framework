package com.tny.game.net.exception;

public class SendTimeoutException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Object body;

    public SendTimeoutException(String message) {
        super(message);
    }

    public SendTimeoutException() {
    }

    public SendTimeoutException(Throwable cause) {
        super(cause);
    }

    public SendTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }


}
