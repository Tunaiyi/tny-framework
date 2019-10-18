package com.tny.game.scanner.exception;

/**
 * <p>
 */
public class ClassScanException extends RuntimeException {

    public ClassScanException() {
    }

    public ClassScanException(String message) {
        super(message);
    }

    public ClassScanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassScanException(Throwable cause) {
        super(cause);
    }

    public ClassScanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
