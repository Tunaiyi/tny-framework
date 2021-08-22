package com.tny.game.relay.transport;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 12:08 下午
 */
public interface NetRelayLinkAllocator {

	NetRelayLink allot(NetTunnel<?> tunnel);

	boolean isClose();

	boolean isActive();

}