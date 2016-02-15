package com.tny.game.actor.event;

import java.util.Map;

/**
 * 错误级别日志事件
 * Created by Kun Yang on 16/1/18.
 */
public class Error extends LogEvent {

    private Throwable cause;

    public static Error error(Object logSource, Class<?> logClass, String message, Object... messageParams) {
        return error(null, logSource, logClass, null, message, messageParams);
    }

    public static Error error(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        return error(null, logSource, logClass, attribute, message, messageParams);
    }

    public static Error error(Throwable cause, Object logSource, Class<?> logClass, String message, Object... messageParams) {
        return error(cause, logSource, logClass, null, message, messageParams);
    }

    public static Error error(Throwable cause, Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        return new Error(cause, logSource, logClass, attribute, message, messageParams);
    }

    private Error(Throwable cause, Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        super(logSource, logClass, attribute, message, messageParams);
        this.cause = cause;
    }

    @Override
    public LogLevel getLevel() {
        return LogLevel.ERROR;
    }

    public Throwable getCause() {
        return cause;
    }

}
