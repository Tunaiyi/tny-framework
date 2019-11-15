package com.tny.game.common.config;


import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 子Config
 * Created by Kun Yang on 16/1/19.
 */
class ChildConfig implements Config {

    private Config parent;

    private String parentKey;

    private String subKeyHead;

    ChildConfig(String parentKey, String delimiter, Config parent) {
        Throws.checkNotNull(StringUtils.isBlank(parentKey), "parentKey 不可为null或为空字符串");
        Throws.checkNotNull(parent, "parent 不可为null");
        this.parent = parent;
        this.parentKey = parentKey;
        String del = delimiter == null ? "." : delimiter;
        this.subKeyHead = parentKey + del;
    }

    private ChildConfig(String parentKey, String subKeyHead, String delimiter, Config parent) {
        Throws.checkNotNull(StringUtils.isBlank(parentKey), "parentKey 不可为null或为空字符串");
        Throws.checkNotNull(parent, "parent 不可为null");
        this.parent = parent;
        this.parentKey = parentKey;
        String del = delimiter == null ? "." : delimiter;
        this.subKeyHead = subKeyHead + del;
    }


    @Override
    public Config child(String key) {
        return child(key, null);
    }

    @Override
    public Config child(String key, String delimiter) {
        Throws.checkArgument(key.startsWith(this.subKeyHead), "{} 不属于 {} 的子 key", key, this.subKeyHead);
        String subKey = key.replace(this.subKeyHead, "");
        return new ChildConfig(key, subKey, delimiter, this);
    }

    @Override
    public Optional<Config> getParent() {
        return Optional.of(parent);
    }

    @Override
    public String parentKey() {
        return parentKey;
    }

    @Override
    public String parentHeadKey() {
        return subKeyHead;
    }

    @Override
    public String getString(String key) {
        return parent.getString(key(key));
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
                          .filter((e) -> e.getKey().startsWith(subKeyHead))
                          .collect(Collectors.toSet());
    }

    @Override
    public Set<String> keySet() {
        return this.parent.keySet().stream()
                          .filter((e) -> e.startsWith(subKeyHead))
                          .collect(Collectors.toSet());
    }

    @Override
    public Collection<Object> values() {
        return this.parent.entrySet().stream()
                          .filter(e -> e.getKey().startsWith(subKeyHead))
                          .map(Entry::getValue)
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
        return this.subKeyHead + childKey;
    }

}
