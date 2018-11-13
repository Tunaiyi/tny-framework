package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Client;

@FunctionalInterface
public interface ClientCloseListener<UID> extends ClientListener {

    void onClose(Client<UID> client);

}
