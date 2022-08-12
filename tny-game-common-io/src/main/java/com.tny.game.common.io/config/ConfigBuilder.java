/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.io.config;

import java.util.*;

/**
 * 配置文件构造器
 * Created by Kun Yang on 16/1/21.
 */
public class ConfigBuilder {

    private Map<String, Object> properties = new HashMap<>();

    private List<ConfigFormatter> formatters = new ArrayList<>();

    public static ConfigBuilder newBuilder() {
        return new ConfigBuilder();
    }

    private ConfigBuilder() {
    }

    public ConfigBuilder put(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public ConfigBuilder put(Map<String, ?> map) {
        this.properties.putAll(map);
        return this;
    }

    public ConfigBuilder addFormatter(ConfigFormatter formatter) {
        this.formatters.add(formatter);
        return this;
    }

    public ConfigBuilder addFormatter(Collection<ConfigFormatter> formatters) {
        this.formatters.addAll(formatters);
        return this;
    }

    public Config build() {
        return new PropertiesConfig(this.properties, this.formatters.toArray(new ConfigFormatter[this.formatters.size()]));
    }

}
