/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
