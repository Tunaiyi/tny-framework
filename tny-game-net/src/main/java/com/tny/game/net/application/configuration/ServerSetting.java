package com.tny.game.net.application.configuration;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/28 09:38
 **/
public interface ServerSetting extends ServiceSetting {

    String getScheme();

    String getBindAddress();

    InetSocketAddress bindAddress();

}

