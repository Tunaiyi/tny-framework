package com.tny.game.actor.event;


import java.util.Map;

/**
 * 警告等级时间
 * Created by Kun Yang on 16/1/18.
 */
public class Warning extends LogEvent {

    private Throwable cause;

    public static Warning warning(Object logSource, Class<?> logClass, String message, Object... messageParams) {
        return warning(null, logSource, logClass, null, message, messageParams);
    }

    public static Warning warning(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        return warning(null, logSource, logClass, attribute, message, messageParams);
    }

    public static Warning warning(Throwable cause, Object logSource, Class<?> logClass, String message, Object... messageParams) {
        return warning(cause, logSource, logClass, null, message, messageParams);
    }

    public static Warning warning(Throwable cause, Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        return new Warning(cause, logSource, logClass, attribute, message, messageParams);
    }

    private Warning(Throwable cause, Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        super(logSource, logClass, attribute, message, messageParams);
        this.cause = cause;
    }

    @Override
    public LogLevel getLevel() {
        return LogLevel.WARNING;
    }

    public Throwable getCause() {
        return cause;
    }

}
