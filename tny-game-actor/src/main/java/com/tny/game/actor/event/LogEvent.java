package com.tny.game.actor.event;


import java.util.HashMap;
import java.util.Map;

/**
 * 日志时间
 * Created by Kun Yang on 16/1/17.
 */
public abstract class LogEvent {

    private Thread thread = Thread.currentThread();

    private long timestamp = System.currentTimeMillis();

    private Object logSource;

    private Class<?> logClass;

    private String message;

    private Object[] messageParams;

    private Map<String, Object> attribute;

    public LogEvent(Object logSource, Class<?> logClass, String message, Object... messageParams) {
        this(logSource, logClass, null, message, messageParams);
    }

    public LogEvent(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        this.logSource = logSource;
        this.logClass = logClass;
        this.message = message;
        this.attribute = attribute;
        this.messageParams = messageParams;
    }

    public Object getLogSource() {
        return logSource;
    }

    public Class<?> getLogClass() {
        return logClass;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }

    public Map<String, Object> getAttribute() {
        if (attribute == null)
            attribute = new HashMap<>();
        return attribute;
    }

    public abstract LogLevel getLevel();

}
