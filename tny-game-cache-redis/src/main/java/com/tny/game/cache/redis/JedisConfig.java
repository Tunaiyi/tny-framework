package com.tny.game.cache.redis;

import com.google.common.base.MoreObjects;
import redis.clients.jedis.Protocol;

import java.util.*;

/**
 * Created by Kun Yang on 2018/2/9.
 */
public class JedisConfig {

    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;
    private String password = null;
    private int db = Protocol.DEFAULT_DATABASE;
    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private Map<String, Object> params = new HashMap<>();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void putParam(String param, Object value) {
        this.params.put(param, value);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("host", host)
                .add("port", port)
                .add("password", password)
                .add("db", db)
                .add("timeout", timeout)
                .add("params", params)
                .toString();
    }
}
