package com.tny.game.net.session.listener;

import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.Tunnel;

import java.rmi.server.UID;

public interface SessionHolderListener {

    default void onAddSession(SessionHolder holder, Session<Object> session) {
    }

    default void onRemoveSession(SessionHolder holder, Session<Object> session) {
    }

    default void onOnline(SessionHolder holder, Session<UID> session, Tunnel<UID> tunnel) {
    }

    default void onOffline(SessionHolder holder, Session<UID> session, Tunnel<UID> tunnel) {
    }

    default void onClose(SessionHolder holder, Session<UID> session, Tunnel<UID> tunnel) {
    }

}
