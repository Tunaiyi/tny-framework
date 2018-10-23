package com.tny.game.net.transport.listener;

import com.tny.game.net.session.*;

public interface SessionHolderListener<UID> {

    default void onAddSession(SessionKeeper<UID> holder, Session<UID> session) {
    }

    default void onRemoveSession(SessionKeeper<UID> holder, Session<UID> session) {
    }

}
