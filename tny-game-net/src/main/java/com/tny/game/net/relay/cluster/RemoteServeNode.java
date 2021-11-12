package com.tny.game.net.relay.cluster;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class RemoteServeNode extends BaseServeNode {

	public RemoteServeNode() {
	}

	public RemoteServeNode(String appType, String scopeType, String serveName, NetAccessPoint point) {
		super(appType, scopeType, serveName, point);
	}

	public RemoteServeNode(String serveName, String appType, String scopeType, long id, String scheme, String host, int port) {
		super(serveName, appType, scopeType, id, scheme, host, port);
	}

}
