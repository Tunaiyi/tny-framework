package com.tny.game.net.netty4.network;

import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 4:30 下午
 */
public class TestGeneralClientTunnel extends GeneralClientTunnel<Long, MockNettyClient> {

	public TestGeneralClientTunnel(long id, NetBootstrapContext bootstrapContext) {
		super(id, bootstrapContext);
	}

	@Override
	protected void setEndpoint(MockNettyClient endpoint) {
		super.setEndpoint(endpoint);
	}

	@Override
	protected MessageTransporter getTransporter() {
		return super.getTransporter();
	}

}
