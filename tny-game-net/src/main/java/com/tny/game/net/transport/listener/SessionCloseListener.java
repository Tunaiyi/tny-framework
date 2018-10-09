package com.tny.game.net.transport.listener;

import com.tny.game.net.transport.*;

public interface SessionCloseListener<UID> {

    default void onClose(Session<UID> session) {
    }

}
