package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;

@FunctionalInterface
public interface EndpointCloseListener<UID> extends EndpointListener {

    void onClose(Endpoint<UID> endpoint);

}
