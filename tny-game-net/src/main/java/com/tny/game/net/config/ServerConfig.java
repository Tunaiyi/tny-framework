package com.tny.game.net.config;

import com.tny.game.common.config.Config;

import java.net.InetSocketAddress;
import java.util.List;

public interface ServerConfig {

    /**
     * @return 获取配置信息
     */
    Config getConfig();

    /**
     * 获取服务器类型
     *
     * @return 返回服务器类型
     */
    String getScopeType();

    /**
     * 名称获取Ip信息列表
     *
     * @param name 名称
     * @return Ip信息
     */
    List<BindIp> getBindIp(String name);

    /**
     * @param serverName
     * @return
     */
    List<InetSocketAddress> getBindInetSocketAddressList(String serverName);

}
