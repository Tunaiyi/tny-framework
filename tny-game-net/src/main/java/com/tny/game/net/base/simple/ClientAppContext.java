package com.tny.game.net.base.simple;

import com.tny.game.net.dispatcher.ServerSessionFactory;
import com.tny.game.net.dispatcher.message.simple.SimpleSessionFactory;

public class ClientAppContext extends AbstractAppContext {

    private String serverType;

    public ClientAppContext() {
    }

    public ClientAppContext(String serverType) {
        this.serverType = serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    @Override
    public String getScopeType() {
        return this.serverType;
    }

    @Override
    protected ServerSessionFactory getDefaultSessionFactory() {
        return new SimpleSessionFactory();
    }

}
