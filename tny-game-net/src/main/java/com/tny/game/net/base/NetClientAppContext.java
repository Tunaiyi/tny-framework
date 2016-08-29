package com.tny.game.net.base;

import com.tny.game.net.base.simple.AbstractNetAppContext;

public class NetClientAppContext extends AbstractNetAppContext {

    private String scopeType;

    public NetClientAppContext(String scopeType) {
        this.scopeType = scopeType;
    }

    @Override
    public String getScopeType() {
        return null;
    }

}
