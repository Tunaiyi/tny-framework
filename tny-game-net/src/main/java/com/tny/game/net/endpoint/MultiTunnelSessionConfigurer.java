package com.tny.game.net.endpoint;


/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-09 10:54
 */
public interface MultiTunnelSessionConfigurer {

    /**
     * @return 接受 Tunnel 最大数量
     */
    int getMaxTunnelSize();

}
