package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.cluster.*;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 3:42 下午
 */
public class RelayServeInstanceSetting extends BaseServeNode {

	public RelayServeInstanceSetting() {
	}

	public RelayServeInstanceSetting(String serveName, String appType, String scopeType, long id, String scheme, String host, int port) {
		super(serveName, appType, scopeType, id, scheme, host, port);
	}

	@Override
	public BaseServeNode setServeName(String serveName) {
		return super.setServeName(serveName);
	}

	@Override
	public BaseServeNode setId(long id) {
		return super.setId(id);
	}

	@Override
	public BaseServeNode setAppType(String appType) {
		return super.setAppType(appType);
	}

	@Override
	public BaseServeNode setScopeType(String scopeType) {
		return super.setScopeType(scopeType);
	}

	@Override
	public BaseServeNode setHealthy(boolean healthy) {
		return super.setHealthy(healthy);
	}

	@Override
	public BaseServeNode setScheme(String scheme) {
		return super.setScheme(scheme);
	}

	@Override
	public BaseServeNode setHost(String host) {
		return super.setHost(host);
	}

	@Override
	public BaseServeNode setPort(int port) {
		return super.setPort(port);
	}

	@Override
	public BaseServeNode setMetadata(Map<String, Object> metadata) {
		return super.setMetadata(metadata);
	}

}
