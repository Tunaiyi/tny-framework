/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.rpc.setting;

import com.tny.game.common.url.*;
import com.tny.game.net.serve.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 5:02 下午
 */
public class RpcServiceSetting implements Serve {

    /**
     * rpc服务名
     */
    private String service;

    /**
     * 服务发现-服务器服务名
     */
    private String serveName;

    private boolean discovery = false;

    private String password = "";

    private String host;

    private int port;

    private String guide;

    private String username;

    private int connectSize = 1;

    private long connectTimeout = 10000L;

    private long authenticateTimeout = 10000L;

    public boolean isDiscovery() {
        return discovery || StringUtils.isNoneBlank(serveName);
    }

    @Override
    public String getServeName() {
        return serveName;
    }

    @Override
    public String getService() {
        return service;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getGuide() {
        return guide;
    }

    public boolean isHasGuide() {
        return StringUtils.isNotEmpty(guide);
    }

    public int getConnectSize() {
        return connectSize;
    }

    public String getUsername() {
        return username;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public long getAuthenticateTimeout() {
        return authenticateTimeout;
    }

    public Optional<URL> url() {
        if (this.isDiscovery()) {
            return Optional.empty();
        }
        return Optional.of(URL.valueOf(format("rpc://{}:{}", this.getHost(), this.getPort())));
    }

    public RpcServiceSetting setServeName(String serveName) {
        this.serveName = serveName;
        return this;
    }

    public RpcServiceSetting setPassword(String password) {
        this.password = password;
        return this;
    }

    public RpcServiceSetting setHost(String host) {
        this.host = host;
        return this;
    }

    public RpcServiceSetting setPort(int port) {
        this.port = port;
        return this;
    }

    public RpcServiceSetting setConnectSize(int connectSize) {
        this.connectSize = connectSize;
        return this;
    }

    public RpcServiceSetting setService(String service) {
        this.service = service;
        return this;
    }

    public RpcServiceSetting setUsername(String username) {
        this.username = username;
        return this;
    }

    public RpcServiceSetting setGuide(String guide) {
        this.guide = guide;
        return this;
    }

    public RpcServiceSetting setDiscovery(boolean discovery) {
        this.discovery = discovery;
        return this;
    }

    public RpcServiceSetting setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RpcServiceSetting setAuthenticateTimeout(long authenticateTimeout) {
        this.authenticateTimeout = authenticateTimeout;
        return this;
    }

}
