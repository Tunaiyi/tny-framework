package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.Session;
import com.tny.game.net.transport.Tunnel;

public interface SessionListener<UID> {

    default void onOnline(Session<UID> session, Tunnel<UID> tunnel) {
    }

    default void onOffline(Session<UID> session, Tunnel<UID> tunnel) {
    }

    default void onClose(Session<UID> session, Tunnel<UID> tunnel) {
    }

}
