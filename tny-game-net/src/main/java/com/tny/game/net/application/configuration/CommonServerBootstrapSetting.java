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
            this.bindAddress = new InetSocketAddress(hostPort[0], NumberUtils.toInt(hostPort[1]));
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
