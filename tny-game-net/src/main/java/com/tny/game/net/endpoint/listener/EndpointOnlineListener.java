package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;

@FunctionalInterface
public interface EndpointOnlineListener<UID> extends EndpointListener {

    void onOnline(Endpoint<UID> endpoint);

}
