package com.tny.game.net.exception;

public class TunnelWriteException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Object body;

    public TunnelWriteException(String message) {
        super(message);
    }

    public TunnelWriteException() {
    }

    public TunnelWriteException(Throwable cause) {
        super(cause);
    }

    public TunnelWriteException(String message, Throwable cause) {
        super(message, cause);
    }


}
