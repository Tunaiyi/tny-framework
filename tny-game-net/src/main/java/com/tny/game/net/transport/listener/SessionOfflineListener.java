package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.*;

public interface SessionOfflineListener<UID> {

    default void onOffline(Session<UID> session) {
    }

}
