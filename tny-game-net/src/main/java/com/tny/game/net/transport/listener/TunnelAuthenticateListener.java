package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.Tunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 16:03
 */
@FunctionalInterface
public interface TunnelAuthenticateListener<UID> {

    void onAuthenticate(Tunnel<UID> tunnel);

}
