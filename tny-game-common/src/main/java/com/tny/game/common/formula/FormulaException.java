package com.tny.game.common.formula;

public class FormulaException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected FormulaException() {
        super();
    }

    protected FormulaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FormulaException(String message, Throwable cause) {
        super(message, cause);
    }

    protected FormulaException(String message) {
        super(message);
    }

    protected FormulaException(Throwable cause) {
        super(cause);
    }

}
