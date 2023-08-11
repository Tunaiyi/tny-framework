package com.tny.game.net.transport;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/13 12:21
 **/
public interface AddressPeer {

    /**
     * @return 远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 本地地址
     */
    InetSocketAddress getLocalAddress();
}
