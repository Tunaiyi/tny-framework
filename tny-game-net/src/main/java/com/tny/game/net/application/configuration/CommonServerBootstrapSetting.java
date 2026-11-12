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
package com.tny.game.net.application.configuration;

import com.tny.game.net.application.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.InetSocketAddress;
import java.util.*;

public class CommonServerBootstrapSetting extends CommonNetBootstrapSetting implements ServerBootstrapSetting {

    private String scheme = "tcp";

    private InetSocketAddress bindAddress;

    private InetSocketAddress serveAddress;

    private String bindAddressValue;

    private String serveAddressValue;

    private Set<String> readIgnoreHeaders = new HashSet<>();

    private Set<String> writeIgnoreHeaders = new HashSet<>();

    public CommonServerBootstrapSetting() {
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getBindServeAddress() {
        return bindAddressValue;
    }

    @Override
    public String getBindAddress() {
        return this.bindAddressValue;
    }

    @Override
    public InetSocketAddress bindAddress() {
        return this.bindAddress;
    }

    @Override
    public InetSocketAddress serveAddress() {
        return serveAddress;
    }


    public void setBindAddress(String address) {
        if (StringUtils.isNoneBlank(address)) {
            this.bindAddressValue = address;
            String[] hostPort = StringUtils.split(address, ":");
            // 配置笔误以可诊断异常拒绝（段数/端口界），不再 AIOOBE/静默 0 端口深入启动栈
            if (hostPort.length != 2) {
                throw new IllegalArgumentException("bind address '" + address + "' 必须为 host:port 形式（不支持 IPv6 字面量）");
            }
            int port = NumberUtils.toInt(hostPort[1], -1);
            if (port < 0 || port > 0xFFFF) {
                throw new IllegalArgumentException("bind address '" + address + "' 端口非法: " + hostPort[1]);
            }
            this.bindAddress = new InetSocketAddress(hostPort[0], port);
        }
    }

    public String getServeAddress() {
        return this.serveAddressValue;
    }

    public void setServeAddress(String address) {
        if (StringUtils.isNoneBlank(address)) {
            this.serveAddressValue = address;
            String[] hostPort = StringUtils.split(address, ":");
            this.serveAddress = new InetSocketAddress(hostPort[0], NumberUtils.toInt(hostPort[1]));
        }
    }

    public CommonServerBootstrapSetting setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    @Override
    public Set<String> getReadIgnoreHeaders() {
        return readIgnoreHeaders;
    }

    @Override
    public Set<String> getWriteIgnoreHeaders() {
        return writeIgnoreHeaders;
    }

    @Override
    public CommonServerBootstrapSetting setReadIgnoreHeaders(Set<String> readIgnoreHeaders) {
        this.readIgnoreHeaders = readIgnoreHeaders;
        return this;
    }

    @Override
    public CommonServerBootstrapSetting setWriteIgnoreHeaders(Set<String> writeIgnoreHeaders) {
        this.writeIgnoreHeaders = writeIgnoreHeaders;
        return this;
    }

}
