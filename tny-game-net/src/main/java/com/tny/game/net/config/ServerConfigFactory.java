package com.tny.game.net.config;

/**
 * 服务器上下文工厂
 *
 * @author KGTny
 */
public interface ServerConfigFactory {

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    public ServerConfig getServerContext();

}
