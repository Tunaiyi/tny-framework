package com.tny.game.basics.develop;

import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.utils.*;
import org.springframework.core.env.Environment;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 2:23 下午
 */
public class DevelopEnvs implements MapAccessor {

    private static final DevelopEnvs envs = new DevelopEnvs(null);

    private static final String KEY_HEAD = "tny.basics.develop.envs.";

    private Environment environment;

    public static DevelopEnvs envs() {
        return envs;
    }

    public static void init(Environment environment) {
        synchronized (envs) {
            if (envs.environment == null) {
                envs.environment = environment;
            }
        }
    }

    private DevelopEnvs(Environment environment) {
        this.environment = environment;
    }

    private String keyOf(String key) {
        return KEY_HEAD + key;
    }

    @Override
    public <T> T getObject(String key) {
        return as(environment.getProperty(keyOf(key), Object.class));
    }

    @Override
    public String getString(String key) {
        return environment.getProperty(keyOf(key));
    }

    @Override
    public String getString(String key, String defaultValue) {
        return environment.getProperty(keyOf(key), defaultValue);
    }

    @Override
    public byte getByte(String key) {
        return ofValue(key, Byte.class);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return environment.getProperty(keyOf(key), Byte.class, defaultValue);
    }

    @Override
    public int getInt(String key) {
        return ofValue(key, Integer.class);
    }

    public <T> T ofValue(String key, Class<T> clazz) {
        key = keyOf(key);
        T value = environment.getProperty(key, clazz);
        Asserts.checkNotNull(value, "{} value is null", key);
        return value;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return environment.getProperty(keyOf(key), Integer.class, defaultValue);
    }

    @Override
    public short getShort(String key) {
        return ofValue(key, Short.class);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return environment.getProperty(keyOf(key), Short.class, defaultValue);
    }

    @Override
    public long getLong(String key) {
        return ofValue(key, Long.class);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return environment.getProperty(keyOf(key), Long.class, defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return ofValue(key, Double.class);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return environment.getProperty(keyOf(key), Double.class, defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return ofValue(key, Float.class);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return environment.getProperty(keyOf(key), Float.class, defaultValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return ofValue(key, Boolean.class);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return environment.getProperty(keyOf(key), Boolean.class, defaultValue);
    }

    @Override
    public Map<String, Object> toMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

}
