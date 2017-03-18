package com.tny.game.net.base;

import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.config.properties.PropertiesServerConfigFactory;

public class DefaultNetServerAppContext extends AbstractNetAppContext implements NetServerAppContext {

    private ServerConfig serverConfig;

    public DefaultNetServerAppContext() {
        this.serverConfig = new PropertiesServerConfigFactory().getServerContext();
    }

    public DefaultNetServerAppContext(ServerConfigFactory factory) {
        this.serverConfig = factory.getServerContext();
    }

    public void setServerConfigFactory(ServerConfigFactory serverConfigFactory) {
        this.serverConfig = serverConfigFactory.getServerContext();
    }

    @Override
    public String getScopeType() {
        return this.serverConfig.getScopeType();
    }

    @Override
    public ServerConfig getServerConfig() {
        return this.serverConfig;
    }

}
