package com.tny.game.redisson.configuration;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/14 7:53 下午
 */
@ConfigurationProperties(prefix = "tny.datasource.redisson")
public class RedissonDataSourceProperties {

    private boolean enable = true;

    @NestedConfigurationProperty
    private RedisDataSourceSetting setting;

    private Map<String, RedisDataSourceSetting> settings = new HashMap<>();

    public boolean isEnable() {
        return enable;
    }

    public RedissonDataSourceProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public RedisDataSourceSetting getSetting() {
        return setting;
    }

    public RedissonDataSourceProperties setSetting(RedisDataSourceSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, RedisDataSourceSetting> getSettings() {
        return settings;
    }

    public RedissonDataSourceProperties setSettings(Map<String, RedisDataSourceSetting> settings) {
        this.settings = settings;
        return this;
    }

}
