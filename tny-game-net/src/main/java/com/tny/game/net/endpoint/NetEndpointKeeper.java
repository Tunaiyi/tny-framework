package com.tny.game.net.endpoint;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2022/1/16 11:40 PM
 */
public interface NetEndpointKeeper<UID, E extends Endpoint<UID>> extends EndpointKeeper<UID, E> {

	default void notifyEndpointOnline(Endpoint<?> endpoint) {
	}

	default void notifyEndpointOffline(Endpoint<?> endpoint) {
	}

	default void notifyEndpointClose(Endpoint<?> endpoint) {
	}

}
