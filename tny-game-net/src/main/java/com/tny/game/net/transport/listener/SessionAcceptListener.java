package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.*;

public interface SessionAcceptListener<UID> {

    default void onAccept(Session<UID> session, Tunnel<UID> tunnel) {
    }

}
