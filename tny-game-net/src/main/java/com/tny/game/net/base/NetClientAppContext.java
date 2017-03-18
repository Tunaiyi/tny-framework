package com.tny.game.net.base;

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
