package com.tny.game.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tny.game.common.utils.json.JSONAide;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

class PropertiesConfig implements Config {

    private static final TypeReference<Set<String>> IMPORT_PATH_TOKEN = new TypeReference<Set<String>>() {
    };

    private List<ConfigFormatter> formatterList = new CopyOnWriteArrayList<>();

    private Map<String, Object> configMap = new ConcurrentHashMap<>();

    private List<ConfigReload> reloadList = new CopyOnWriteArrayList<>();

    public static final String IMPORT_KEY = "tny.config.import";

    protected PropertiesConfig(Map<?, ?> properties, ConfigFormatter... formatters) {
        if (formatters != null && formatters.length > 0)
            this.formatterList.addAll(Arrays.asList(formatters));
        this.reload(properties);
    }

    public void reload(Map<?, ?> properties) {
        Map<String, Object> configMap = new ConcurrentHashMap<>();
        Set<String> imports = new HashSet<>();
        for (Entry<?, ?> entry : properties.entrySet()) {
            if (entry.getKey().equals(IMPORT_KEY))
                imports = JSONAide.toObject(entry.getValue().toString(), IMPORT_PATH_TOKEN);
            Object value = entry.getValue().toString();
            String key = entry.getKey().toString();
            for (ConfigFormatter formatter : this.formatterList)
                if (formatter.isKey(key))
                    value = formatter.formatObject(value.toString());
            configMap.put(key, value);
        }
        for (String importFile : imports) {
            Config subConfig = ConfigLib.getConfig(importFile, this.formatterList.toArray(new ConfigFormatter[0]));
            subConfig.entrySet().forEach((entry) -> configMap.put(entry.getKey(), entry.getValue()));
        }
        this.configMap = configMap;
        for (ConfigReload reloadable : this.reloadList)
            reloadable.reload(this);
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
        return Optional.empty();
    }

    @Override
    public String parentKey() {
        return "";
    }

    @Override
    public String parentHeadKey() {
        return "";
    }

    @Override
    public String getStr(String key) {
        Object object = this.configMap.get(key);
        if (object != null)
            return String.valueOf(object);
        else
            return null;
    }

    @Override
    public String getStr(String key, String defValue) {
        String value = this.getStr(key);
        return StringUtils.isBlank(value) ? defValue : value;
    }

    @Override
    public int getInt(String key) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return 0;
        return Integer.parseInt(value);
    }

    @Override
    public int getInt(String key, int defValue) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return defValue;
        return Integer.parseInt(value);
    }

    @Override
    public long getLong(String key) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return 0;
        return Long.parseLong(value);
    }

    @Override
    public long getLong(String key, long defValue) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return defValue;
        return Long.parseLong(value);
    }

    @Override
    public double getDouble(String key) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return 0.0;
        return Double.parseDouble(value);
    }

    @Override
    public double getDouble(String key, double defValue) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return defValue;
        return Double.parseDouble(value);
    }

    @Override
    public float getFloat(String key) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return 0.0F;
        return Float.parseFloat(value);
    }

    @Override
    public float getFloat(String key, float defValue) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return defValue;
        return Float.parseFloat(value);
    }

    @Override
    public boolean getBoolean(String key) {
        String value = this.getStr(key);
        return !StringUtils.isBlank(value) && Boolean.parseBoolean(value);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return defValue;
        return Boolean.parseBoolean(value);
    }

    @Override
    public byte getByte(String key) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return 0;
        return Byte.parseByte(value);
    }

    @Override
    public byte getByte(String key, byte defValue) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return defValue;
        return Byte.parseByte(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O getObject(String key) {
        Object value = this.configMap.get(key);
        if (value == null)
            return null;
        try {
            return (O) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O getObject(String key, O defValue) {
        Object value = this.getObject(key);
        if (value == null)
            return defValue;
        return (O) value;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(this.configMap.entrySet());
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.configMap.keySet());
    }

    @Override
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.configMap.values());
    }

    @Override
    public void addConfigReload(ConfigReload reloadable) {
        this.reloadList.add(reloadable);
    }

    @Override
    public void removeConfigReload(ConfigReload reloadable) {
        this.reloadList.remove(reloadable);
    }

    @Override
    public void clearConfigReload() {
        this.reloadList.clear();
    }

    @Override
    public <E extends Enum<E>> E getEnum(String key, E defValue) {
        E value = this.getEnum(key, defValue.getDeclaringClass());
        return value == null ? defValue : value;
    }

    @Override
    public <E extends Enum<E>> E getEnum(String key, Class<E> enumClass) {
        String value = this.getStr(key);
        if (StringUtils.isBlank(value))
            return null;
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public <T> Map<String, T> find(String regular) {
        Pattern pattern = Pattern.compile(regular);
        return this.find(pattern);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> find(Pattern regular) {
        Map<String, T> map = new HashMap<>();
        for (Entry<String, Object> entry : this.configMap.entrySet()) {
            if (regular.matcher(entry.getKey()).matches())
                map.put(entry.getKey(), (T) entry.getValue());
        }
        return map;
    }

}
