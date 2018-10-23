package com.tny.game.net.session;

import com.tny.game.net.session.SessionConfigurer;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-09 10:54
 */
public interface MultiTunnelSessionConfigurer extends SessionConfigurer {

    /**
     * @return 接受 Tunnel 最大数量
     */
    int getMaxTunnelSize();

}
