package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.Tunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 16:03
 */
@FunctionalInterface
public interface TunnelActivateListener<UID> extends TunnelListener {

    void onActivate(Tunnel<UID> session);

}
