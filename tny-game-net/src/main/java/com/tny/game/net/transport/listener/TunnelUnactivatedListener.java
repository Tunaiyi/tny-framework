package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 16:03
 */
public interface TunnelUnactivatedListener<UID> {

	default void onUnactivated(Tunnel<UID> tunnel) {
	}

}
