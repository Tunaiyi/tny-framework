package com.tny.game.net.transport;

import com.tny.game.common.url.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2024/1/5 09:56
 **/
public interface NetClientTunnel extends NetTunnel {

    /**
     * @return 获取客户端 url
     */
    URL getUrl();

    void connect();

}
