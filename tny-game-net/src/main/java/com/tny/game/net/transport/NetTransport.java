package com.tny.game.net.transport;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:45
 */
public interface NetTransport<UID> extends Transport<UID> {

    /**
     * @return 远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * @return 是否活跃
     */
    boolean isActive();

    /**
     * 关闭断开连接
     */
    void close();

    /**
     * 通道通道
     *
     * @param tunnel 通道通道
     */
    void bind(NetTunnel<UID> tunnel);

}
