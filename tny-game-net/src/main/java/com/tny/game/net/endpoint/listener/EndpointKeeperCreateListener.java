package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;

public interface EndpointKeeperCreateListener<UID> {

	void onCreate(EndpointKeeper<UID, Endpoint<UID>> keeper);

}
