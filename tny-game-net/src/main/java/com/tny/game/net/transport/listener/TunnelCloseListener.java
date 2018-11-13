package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.Tunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 16:03
 */
@FunctionalInterface
public interface TunnelCloseListener<UID> {

    void onClose(Tunnel<UID> session);

}
