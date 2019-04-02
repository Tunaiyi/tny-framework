package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;

@FunctionalInterface
public interface ClientCloseListener<UID> extends ClientListener {

    void onClose(Client<UID> client);

}
