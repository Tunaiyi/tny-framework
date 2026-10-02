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
