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

package com.tny.game.net.application;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.context.*;

import java.util.*;

public class DefaultNetAppContext implements NetAppContext {

    private int serverId;

    private String name;

    private String appType = "default";

    private String scopeType = "online";

    private String service = "";

    private String locale = "zh-CN";

    private List<String> scanPackages = ImmutableList.of();

    private final Attributes attributes = ContextAttributes.create();

    public DefaultNetAppContext() {
    }

    @Override
    public String getAppType() {
        return this.appType;
    }

    @Override
    public String getLocale() {
        return this.locale;
    }

    @Override
    public String getScopeType() {
        return this.scopeType;
    }

    @Override
    public int getServerId() {
        return this.serverId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    public List<String> getScanPackages() {
        return this.scanPackages;
    }

    protected DefaultNetAppContext setName(String name) {
        this.name = name;
        return this;
    }

    protected DefaultNetAppContext setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    protected DefaultNetAppContext setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    protected DefaultNetAppContext setScanPackages(Collection<String> scanPackages) {
        this.scanPackages = ImmutableList.copyOf(scanPackages);
        return this;
    }

    protected DefaultNetAppContext setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    protected DefaultNetAppContext setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

}
