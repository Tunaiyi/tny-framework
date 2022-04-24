package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;

public interface EndpointKeeperListener<UID> {

    default void onAddEndpoint(EndpointKeeper<UID, Endpoint<UID>> keeper, Endpoint<UID> endpoint) {
    }

    default void onRemoveEndpoint(EndpointKeeper<UID, Endpoint<UID>> keeper, Endpoint<UID> endpoint) {
    }

}
