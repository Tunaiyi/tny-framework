package com.tny.game.net.relay.link;

import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:47 下午
 */
public interface LocalRelayTunnel<UID> extends NetRelayTunnel<UID> {

	void bindLink(LocalRelayLink link);

	void unbindLink(LocalRelayLink link);

	LocalRelayLink getLink(String clusterId);

	Set<String> getLinkKeys();

}