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

package com.tny.game.data.mongodb.configuration;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/13 8:42 下午
 */
@ConfigurationProperties(prefix = "tny.datasource.mongodb")
public class MongodbDataSourceProperties {

    private boolean enable = true;

    @NestedConfigurationProperty
    private MongodbDataSourceSetting setting;

    private Map<String, MongodbDataSourceSetting> settings = new HashMap<>();

    public boolean isEnable() {
        return enable;
    }

    public MongodbDataSourceProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public MongodbDataSourceSetting getSetting() {
        return setting;
    }

    public MongodbDataSourceProperties setSetting(MongodbDataSourceSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, MongodbDataSourceSetting> getSettings() {
        return settings;
    }

    public MongodbDataSourceProperties setSettings(
            Map<String, MongodbDataSourceSetting> settings) {
        this.settings = settings;
        return this;
    }

}
