package com.tny.game.net.transport.listener;

import com.tny.game.net.session.Session;

public interface SessionOnlineListener<UID> {

    default void onOnline(Session<UID> session) {
    }

}
