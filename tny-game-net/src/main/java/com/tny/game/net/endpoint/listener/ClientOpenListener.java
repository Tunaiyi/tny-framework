package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Client;

@FunctionalInterface
public interface ClientOpenListener<UID> extends ClientListener {

    void onOpen(Client<UID> client);

}
