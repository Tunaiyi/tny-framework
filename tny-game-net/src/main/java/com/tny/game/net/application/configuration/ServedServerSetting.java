package com.tny.game.net.application.configuration;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/28 09:38
 **/
public interface ServedServerSetting extends ServerSetting {


    /**
     * @return 注册服务地址
     */
    String getBindServeAddress();

    /**
     * @return 注册服务端口地址
     */
    InetSocketAddress serveAddress();

}

