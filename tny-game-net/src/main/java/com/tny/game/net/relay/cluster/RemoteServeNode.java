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

	public RemoteServeNode(String appType, String scopeType, String serveName, String service, NetAccessPoint point) {
		super(appType, scopeType, serveName, service, point);
	}

	public RemoteServeNode(String serveName, String service, String appType, String scopeType, long id, String scheme, String host, int port) {
		super(serveName, service, appType, scopeType, id, scheme, host, port);
	}

}
