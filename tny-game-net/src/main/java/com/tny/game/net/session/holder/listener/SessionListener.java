package com.tny.game.net.session.holder.listener;

import com.tny.game.net.session.Session;
import com.tny.game.net.tunnel.Tunnel;

public interface SessionListener<UID> {

    default void onOnline(Session<UID> session, Tunnel<UID> tunnel) {
    }

    default void onOffline(Session<UID> session, Tunnel<UID> tunnel) {
    }

}
