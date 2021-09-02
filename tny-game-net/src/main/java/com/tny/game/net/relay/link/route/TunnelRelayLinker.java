package com.tny.game.net.relay.link.route;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 5:27 下午
 */
public class TunnelRelayLinker {

	private final LocalRelayLink link;

	private final ServeClusterImportance importance;

	public TunnelRelayLinker(LocalRelayLink link, ServeClusterImportance importance) {
		this.link = link;
		this.importance = importance;
	}

	public LocalRelayLink link() {
		return link;
	}

	public ServeClusterImportance getImportance() {
		return importance;
	}

}
