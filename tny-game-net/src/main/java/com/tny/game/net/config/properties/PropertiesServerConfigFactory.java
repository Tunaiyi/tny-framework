package com.tny.game.net.config.properties;

import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;

/**
 * 服务器属性上下文工厂 所有PropertiesServiceContextFactory对象调用getInstance()
 * 都会返回同一个context对象
 *
 * @author KGTny
 */
public class PropertiesServerConfigFactory implements ServerConfigFactory {

    private PropertiesServerConfig CONTEXT;

    public PropertiesServerConfigFactory() {
        this.initFactory("service.properties");
    }

    public PropertiesServerConfigFactory(String propertiesFile) {
        this.initFactory(propertiesFile);
    }

    private void initFactory(String propertiesFile) {
        this.CONTEXT = new PropertiesServerConfig(propertiesFile);
        this.CONTEXT.load();
    }

    @Override
    public ServerConfig getServerContext() {
        return this.CONTEXT;
    }

}
