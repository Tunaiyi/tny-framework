package com.tny.game.net.transport.listener;

import com.tny.game.net.session.Session;

public interface SessionOfflineListener<UID> {

    default void onOffline(Session<UID> session) {
    }

}
