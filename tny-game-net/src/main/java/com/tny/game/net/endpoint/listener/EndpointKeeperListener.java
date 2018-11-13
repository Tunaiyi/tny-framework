package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.Endpoint;

public interface EndpointKeeperListener<UID> {

    default void onAddEndpoint(EndpointKeeper<UID, Endpoint<UID>> holder, Endpoint<UID> endpoint) {
    }

    default void onRemoveEndpoint(EndpointKeeper<UID, Endpoint<UID>> holder, Endpoint<UID> endpoint) {
    }

}
