package com.tny.game.net.session.holder.listener;

import com.tny.game.net.session.Session;
import com.tny.game.net.session.holder.SessionHolder;

public interface SessionHolderListener {

    default void onAddSession(SessionHolder holder, Session<Object> session) {
    }

    default void onRemoveSession(SessionHolder holder, Session<Object> session) {
    }

}
