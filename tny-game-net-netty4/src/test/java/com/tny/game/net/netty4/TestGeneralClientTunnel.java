package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 4:30 下午
 */
public class TestGeneralClientTunnel extends GeneralClientTunnel<Long, MockNettyClient> {

	public TestGeneralClientTunnel(long id, NetBootstrapContext<Long> bootstrapContext) {
		super(id, bootstrapContext);
	}

	@Override
	protected AbstractTunnel<Long, MockNettyClient> setEndpoint(MockNettyClient endpoint) {
		return super.setEndpoint(endpoint);
	}

	@Override
	protected MessageTransporter<Long> getTransporter() {
		return super.getTransporter();
	}

}
