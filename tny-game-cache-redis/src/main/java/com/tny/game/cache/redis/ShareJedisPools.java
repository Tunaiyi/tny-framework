package com.tny.game.cache.redis;

import com.google.common.collect.*;
import com.tny.game.common.config.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ShareJedisPools {

    private JedisPool defaultJedis;
    private Map<String, JedisPool> jedisPoolMap = ImmutableMap.of();

    private static final String DEFAULT = "default";

    private static final String HEAD = "redis.datasource.";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String DB_KEY = "db";
    private static final String TIMEOUT_KEY = "timeout";
    private static final String PASSWORD_KEY = "password";
    private static final String PARAM_KEY = "param";

    private static Set<String> KEYS = ImmutableSet.of(HOST_KEY, PORT_KEY, DB_KEY, TIMEOUT_KEY, PASSWORD_KEY, PARAM_KEY);

    public ShareJedisPools(String... paths) throws IOException {
        Properties properties = new Properties();
        for (String path : paths)
            properties.load(ConfigLoader.loadInputStream(path));
        Map<String, JedisConfig> configMap = new HashMap<>();
        Map<String, JedisPool> jedisPoolMap = new HashMap<>();
        Config config = ConfigLib.newConfig(properties);
        for (Entry<String, Object> entry : config.entrySet()) {
            try {

                String[] keyWords = StringUtils.split(entry.getKey(), ".");
                String dbName = keyWords[2];
                if (KEYS.contains(dbName)) {
                    JedisConfig jedisConfig = configMap.computeIfAbsent(DEFAULT, k -> new JedisConfig());
                    if (dbName.equals(PARAM_KEY)) {
                        jedisConfig.putParam(keyWords[3], entry.getValue());
                    } else {
                        BeanUtils.setProperty(jedisConfig, dbName, entry.getValue());
                    }
                } else {
                    JedisConfig jedisConfig = configMap.computeIfAbsent(dbName, k -> new JedisConfig());
                    String keyWord = keyWords[3];
                    if (keyWord.equals(PARAM_KEY)) {
                        jedisConfig.putParam(keyWords[4], entry.getValue());
                    } else {
                        BeanUtils.setProperty(jedisConfig, keyWord, entry.getValue());
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        configMap.forEach((name, jedisConfig) -> {
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            try {
                BeanUtils.populate(poolConfig, jedisConfig.getParams());
                JedisPool jedisPool = new JedisPool(poolConfig,
                        jedisConfig.getHost(),
                        jedisConfig.getPort(),
                        jedisConfig.getTimeout(),
                        jedisConfig.getPassword(),
                        jedisConfig.getDb());
                try (Jedis jedis = jedisPool.getResource()) {
                    jedisPoolMap.put(name, jedisPool);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });
        this.jedisPoolMap = ImmutableMap.copyOf(jedisPoolMap);
        jedisPoolMap.forEach((k, p) -> {
            System.out.println(k + " : " + p);
        });
    }

    public static void main(String[] args) throws IOException {
        ShareJedisPools pools = new ShareJedisPools("share_jedis_pools.properties");

    }

}
