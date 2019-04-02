package com.tny.game.net.base.configuration;


import com.tny.game.common.context.*;
import com.tny.game.net.base.AppContext;

import java.util.*;

public class DefaultAppContext implements AppContext {

    private String name;

    private String appType = "default";

    private String scopeType = "online";

    private String[] scanPaths;

    private Attributes attributes = ContextAttributes.create();

    public DefaultAppContext() {
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public String getScopeType() {
        return scopeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    public String[] getScanPathArray() {
        return this.scanPaths;
    }

    @Override
    public List<String> getScanPathList() {
        return Arrays.asList(this.scanPaths);
    }

    public DefaultAppContext setName(String name) {
        this.name = name;
        return this;
    }

    public DefaultAppContext setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public DefaultAppContext setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public DefaultAppContext setScanPaths(String[] scanPaths) {
        this.scanPaths = scanPaths;
        return this;
    }
}
