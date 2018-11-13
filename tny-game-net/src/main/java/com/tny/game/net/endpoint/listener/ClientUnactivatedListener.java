package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Client;
import com.tny.game.net.transport.Tunnel;

@FunctionalInterface
public interface ClientUnactivatedListener<UID> extends ClientListener {

    void onUnactivated(Client<UID> session, Tunnel<UID> tunnel);

}
