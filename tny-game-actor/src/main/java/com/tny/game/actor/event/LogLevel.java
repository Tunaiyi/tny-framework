package com.tny.game.actor.event;

/**
 * 日志等级
 * Created by Kun Yang on 16/1/18.
 */
public enum LogLevel {

    ERROR(1),

    WARNING(2),

    INFO(3),

    DEBUG(4);

    private int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int compareLevel(LogLevel level) {
        return this.level - level.level;
    }

    public boolean isCanLog(LogLevel level) {
        return this.compareLevel(level) >= 0;
    }

}
