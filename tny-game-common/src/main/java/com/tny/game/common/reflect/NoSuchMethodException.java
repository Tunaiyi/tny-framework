package com.tny.game.common.reflect;

public class NoSuchMethodException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NoSuchMethodException() {
        super();
    }

    public NoSuchMethodException(String message) {
        super(message);
    }

    public NoSuchMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
