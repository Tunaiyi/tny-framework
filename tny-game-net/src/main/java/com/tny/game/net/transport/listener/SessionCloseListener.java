package com.tny.game.net.transport.listener;

import com.tny.game.net.session.Session;

public interface SessionCloseListener<UID> {

    default void onClose(Session<UID> session) {
    }

}
