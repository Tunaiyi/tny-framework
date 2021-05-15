package com.tny.game.net.base.configuration;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;

import java.util.*;

public class DefaultNetAppContext implements NetAppContext {

    private String name;

    private String appType = "default";

    private String scopeType = "online";

    private List<String> scanPackages;

    private Attributes attributes = ContextAttributes.create();

    public DefaultNetAppContext() {
    }

    @Override
    public String getAppType() {
        return this.appType;
    }

    @Override
    public String getScopeType() {
        return this.scopeType;
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
        return Collections.unmodifiableList(this.scanPackages);
    }

    public DefaultNetAppContext setName(String name) {
        this.name = name;
        return this;
    }

    public DefaultNetAppContext setAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public DefaultNetAppContext setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public DefaultNetAppContext setScanPackages(String[] scanPackages) {
        this.scanPackages = Arrays.asList(scanPackages);
        return this;
    }

}
