package com.tny.game.net.relay.cluster;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class LocalServeNode extends BaseServeNode {

	public LocalServeNode(NetAppContext netAppContext, String serveName, String scheme, String host, int port) {
		super(serveName, netAppContext.getAppType(), netAppContext.getScopeType(), netAppContext.getServerId(),
				scheme, host, port);
		this.setHealthy(true);
	}

}
