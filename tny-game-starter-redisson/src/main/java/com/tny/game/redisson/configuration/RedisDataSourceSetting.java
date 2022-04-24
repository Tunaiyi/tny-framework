package com.tny.game.redisson.configuration;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/14 7:55 下午
 */
public class RedisDataSourceSetting extends RedisProperties {

    private boolean templateEnable = false;

    private boolean stringTemplateEnable = false;

    public boolean isTemplateEnable() {
        return templateEnable;
    }

    public RedisDataSourceSetting setTemplateEnable(boolean templateEnable) {
        this.templateEnable = templateEnable;
        return this;
    }

    public boolean isStringTemplateEnable() {
        return stringTemplateEnable;
    }

    public RedisDataSourceSetting setStringTemplateEnable(boolean stringTemplateEnable) {
        this.stringTemplateEnable = stringTemplateEnable;
        return this;
    }

}
