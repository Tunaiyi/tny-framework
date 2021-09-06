package com.tny.game.net.endpoint;

/**
 * <p>
 */
public class CommonSessionFactory<UID> implements SessionFactory<UID, CommonSession<UID>, SessionSetting> {

	public CommonSessionFactory() {
	}

	@Override
	public CommonSession<UID> create(SessionSetting setting, EndpointContext endpointContext) {
		return new CommonSession<>(setting, endpointContext);
	}

}
