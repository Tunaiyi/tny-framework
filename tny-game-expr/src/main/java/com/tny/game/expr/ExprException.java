package com.tny.game.expr;

public class ExprException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected ExprException() {
        super();
    }

    protected ExprException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ExprException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ExprException(String message) {
        super(message);
    }

    protected ExprException(Throwable cause) {
        super(cause);
    }

}
