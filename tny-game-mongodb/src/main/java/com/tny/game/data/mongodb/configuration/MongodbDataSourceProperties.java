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
