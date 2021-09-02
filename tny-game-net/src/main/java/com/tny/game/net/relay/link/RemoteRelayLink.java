package com.tny.game.net.relay.link;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 10:13 下午
 */
public interface RemoteRelayLink extends NetRelayLink {

	<UID> NetTunnel<UID> acceptTunnel(long tunnelId, String host, int port);

}
