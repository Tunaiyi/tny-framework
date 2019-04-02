package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

@FunctionalInterface
public interface ClientUnactivatedListener<UID> extends ClientListener {

    void onUnactivated(Client<UID> session, Tunnel<UID> tunnel);

}
