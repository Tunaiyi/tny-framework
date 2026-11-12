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
        // 防御复制：不把构建器活映射交入配置（后续 put 穿透/并发读竞态）
        return new PropertiesConfig(new HashMap<>(this.properties),
                this.formatters.toArray(new ConfigFormatter[this.formatters.size()]));
    }

}
