/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
