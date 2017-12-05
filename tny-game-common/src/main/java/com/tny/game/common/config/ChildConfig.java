package com.tny.game.common.config;

import com.tny.game.suite.base.Throws;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 子Config
 * Created by Kun Yang on 16/1/19.
 */
class ChildConfig implements Config {

    private Config parent;

    private String parentKey;

    private String parentHeadKey;

    ChildConfig(String parentKey, String delimiter, Config parent) {
        Throws.checkNotNull(StringUtils.isBlank(parentKey), "parentKey 不可为null或为空字符串");
        Throws.checkNotNull(parent, "parent 不可为null");
        this.parent = parent;
        this.parentKey = parentKey;
        String del = delimiter == null ? "." : delimiter;
        this.parentHeadKey = parentKey + del;
    }


    @Override
    public Config child(String key) {
        return child(key, null);
    }

    @Override
    public Config child(String key, String delimiter) {
        return new ChildConfig(key, delimiter, this);
    }

    @Override
    public Optional<Config> getParent() {
        return parent == null ? Optional.empty() : Optional.of(parent);
    }

    @Override
    public String parentKey() {
        return parentKey;
    }

    @Override
    public String parentHeadKey() {
        return parentHeadKey;
    }

    @Override
    public String getStr(String key) {
        return parent.getStr(key(key));
    }

    @Override
    public String getStr(String key, String defValue) {
        return parent.getStr(key(key), defValue);
    }

    @Override
    public int getInt(String key) {
        return parent.getInt(key(key));
    }

    @Override
    public int getInt(String key, int defValue) {
        return parent.getInt(key(key), defValue);
    }

    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return parent.getLong(key(key), defValue);
    }

    @Override
    public double getDouble(String key) {
        return 0;
    }

    @Override
    public double getDouble(String key, double defValue) {
        return parent.getDouble(key(key), defValue);
    }

    @Override
    public float getFloat(String key) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return parent.getFloat(key(key), defValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return parent.getBoolean(key(key), defValue);
    }

    @Override
    public byte getByte(String key) {
        return 0;
    }

    @Override
    public byte getByte(String key, byte defValue) {
        return parent.getByte(key(key), defValue);
    }

    @Override
    public <O> O getObject(String key) {
        return null;
    }

    @Override
    public <O> O getObject(String key, O defValue) {
        return parent.getObject(key(key), defValue);
    }

    @Override
    public <E extends Enum<E>> E getEnum(String key, E defValue) {
        return parent.getEnum(key(key), defValue);
    }

    @Override
    public <E extends Enum<E>> E getEnum(String key, Class<E> enumClass) {
        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.parent.entrySet().stream()
                .filter((e) -> e.getKey().startsWith(parentHeadKey))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> keySet() {
        return this.parent.keySet().stream()
                .filter((e) -> e.startsWith(parentHeadKey))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Object> values() {
        return this.parent.entrySet().stream()
                .filter(e -> e.getKey().startsWith(parentHeadKey))
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public void addConfigReload(ConfigReload reload) {
        this.parent.addConfigReload(reload);
    }

    @Override
    public void removeConfigReload(ConfigReload reload) {
        this.parent.removeConfigReload(reload);
    }

    @Override
    public void clearConfigReload() {
        this.parent.clearConfigReload();
    }

    @Override
    public <T> Map<String, T> find(String regular) {
        return this.parent.find(regular);
    }

    @Override
    public <T> Map<String, T> find(Pattern regular) {
        return this.parent.find(regular);
    }

    private String key(String childKey) {
        return parentHeadKey + childKey;
    }
}
