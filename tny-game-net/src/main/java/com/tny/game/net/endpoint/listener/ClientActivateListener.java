package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Client;
import com.tny.game.net.transport.Tunnel;

@FunctionalInterface
public interface ClientActivateListener<UID> extends ClientListener {

    void onActivate(Client<UID> session, Tunnel<UID> tunnel);

}
