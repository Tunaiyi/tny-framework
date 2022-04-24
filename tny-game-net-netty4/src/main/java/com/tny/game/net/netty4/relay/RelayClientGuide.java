package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 3:31 下午
 */
public interface RelayClientGuide {

    /**
     * @return 是否关闭
     */
    boolean isClosed();

    /**
     * @return 关闭
     */
    boolean close();

    /**
     * @param url url
     * @return 返回客户端
     */
    default RelayTransporter connect(URL url) {
        return connect(url, -1);
    }

    /**
     * @param url url
     * @return 返回客户端
     */
    RelayTransporter connect(URL url, long timeout);

    /**
     * @param url url
     */
    default void connect(URL url, RelayConnectCallback callback) {
        connect(url, -1, callback);
    }

    /**
     * @param url url
     */
    void connect(URL url, long timeout, RelayConnectCallback callback);

}
