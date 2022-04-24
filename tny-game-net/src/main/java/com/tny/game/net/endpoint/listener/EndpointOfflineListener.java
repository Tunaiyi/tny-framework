package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;

@FunctionalInterface
public interface EndpointOfflineListener<UID> extends EndpointListener {

    void onOffline(Endpoint<UID> endpoint);

}
