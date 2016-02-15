package com.tny.game.actor.event;

import java.util.Map;

/**
 * 消息等级日志事件
 * Created by Kun Yang on 16/1/18.
 */
public class Info extends LogEvent {

    public static Info info(Object logSource, Class<?> logClass, String message, Object ... messageParams) {
        return info(logSource, logClass, null, message, messageParams);
    }

    public static Info info(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object ... messageParams) {
        return new Info(logSource, logClass, attribute, message, messageParams);
    }

    private Info(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object ... messageParams) {
        super(logSource, logClass, attribute, message, messageParams);
    }

    @Override
    public LogLevel getLevel() {
        return LogLevel.INFO;
    }


}
