package com.tny.game.common.config;


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
        return new PropertiesConfig(properties, formatters.toArray(new ConfigFormatter[formatters.size()]));
    }
}
