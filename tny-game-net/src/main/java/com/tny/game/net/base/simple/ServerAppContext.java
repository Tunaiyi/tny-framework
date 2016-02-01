package com.tny.game.net.base.simple;

import com.tny.game.net.base.ServerContext;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.config.properties.PropertiesServerConfigFactory;
import com.tny.game.net.dispatcher.ServerSessionFactory;
import com.tny.game.net.dispatcher.message.simple.SimpleSessionFactory;

public class ServerAppContext extends AbstractAppContext implements ServerContext {

    private ServerConfig serverConfig;

    public ServerAppContext() {
        this.serverConfig = new PropertiesServerConfigFactory().getServerContext();
    }

    public ServerAppContext(ServerConfigFactory factory) {
        this.serverConfig = factory.getServerContext();
    }

    @Override
    public String getScopeType() {
        return this.serverConfig.getScopeType();
    }

    @Override
    protected ServerSessionFactory getDefaultSessionFactory() {
        return new SimpleSessionFactory();
    }

    public void setServerConfigFactory(ServerConfigFactory serverConfigFactory) {
        this.serverConfig = serverConfigFactory.getServerContext();
    }

    @Override
    public ServerConfig getServerConfig() {
        return this.serverConfig;
    }

}
