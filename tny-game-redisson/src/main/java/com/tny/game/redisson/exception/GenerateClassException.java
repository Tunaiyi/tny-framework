package com.tny.game.redisson.exception;

/**
 * 动态生成类失败
 * <p>
 */
public class GenerateClassException extends RuntimeException {

    public GenerateClassException() {
    }

    public GenerateClassException(String message) {
        super(message);
    }

    public GenerateClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateClassException(Throwable cause) {
        super(cause);
    }

    public GenerateClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
