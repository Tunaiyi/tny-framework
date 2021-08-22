package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class EndpointTestInstance<E extends NetEndpoint<Long>> {

	private E endpoint;

	private MockNetTunnel tunnel;

	public EndpointTestInstance(E endpoint, MockNetTunnel tunnel) {
		this.endpoint = endpoint;
		this.tunnel = tunnel;
	}

	public E getEndpoint() {
		return this.endpoint;
	}

	public MockNetTunnel getTunnel() {
		return this.tunnel;
	}

}
