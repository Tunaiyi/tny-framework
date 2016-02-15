package com.tny.game.actor.system;

import com.tny.game.actor.event.LogLevel;
import com.tny.game.common.config.Config;

/**
 * 设置
 * Created by Kun Yang on 16/1/18.
 */
public class Setting {

    private static final String LOG_LEVEL = "tny.actor.log_level";

    private Config config;

    private LogLevel logLevel;

    public Setting(Config config) {
        this.config = config;
        this.logLevel = config.getEnum(LOG_LEVEL, LogLevel.WARNING);
    }

    public Config getConfig() {
        return config;
    }

    public boolean isCanLog(LogLevel level) {
        return this.logLevel.isCanLog(level);
    }

}
