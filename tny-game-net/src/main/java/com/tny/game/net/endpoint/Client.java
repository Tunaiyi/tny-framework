package com.tny.game.net.endpoint;

import com.tny.game.common.utils.URL;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-10 15:22
 */
public interface Client<UID> extends Endpoint<UID> {

    /**
     * @return 获取客户端 url
     */
    URL getUrl();

    /**
     * 断开连接
     */
    void disconnect();

}
