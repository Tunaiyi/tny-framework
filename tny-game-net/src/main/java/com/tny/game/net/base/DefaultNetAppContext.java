package com.tny.game.net.base;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.context.*;

import java.util.*;

public class DefaultNetAppContext implements NetAppContext {

    private int serverId;

    private String name;

    private String appType = "default";

    private String scopeType = "online";

    private String locale = "zh-CN";

    private List<String> scanPackages = ImmutableList.of();

    private final Attributes attributes = ContextAttributes.create();

    public DefaultNetAppContext() {
        NetAppContextHolder.register(this);
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
