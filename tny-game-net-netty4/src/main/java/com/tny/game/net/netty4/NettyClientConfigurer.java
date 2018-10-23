package com.tny.game.net.netty4;

import com.tny.game.net.session.MultiTunnelSessionConfigurer;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-09 17:45
 */
public interface NettyClientConfigurer extends MultiTunnelSessionConfigurer {

     String getUrl();

}
