package com.tny.game.actor.event;

import java.util.Map;

/**
 * 调试等级日志事件
 * Created by Kun Yang on 16/1/18.
 */
public class Debug extends LogEvent {

    public static Debug debug(Object logSource, Class<?> logClass, String message, Object... messageParams) {
        return debug(logSource, logClass, null, message, messageParams);
    }

    public static Debug debug(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        return new Debug(logSource, logClass, attribute, message, messageParams);
    }

    private Debug(Object logSource, Class<?> logClass, Map<String, Object> attribute, String message, Object... messageParams) {
        super(logSource, logClass, attribute, message, messageParams);
    }

    @Override
    public LogLevel getLevel() {
        return LogLevel.DEBUG;
    }


}
