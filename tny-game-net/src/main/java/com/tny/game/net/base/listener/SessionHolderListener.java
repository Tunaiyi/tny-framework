package com.tny.game.net.base.listener;

import com.tny.game.net.session.Session;
import com.tny.game.net.session.SessionHolder;

public interface SessionHolderListener<T> {

    default void onAddSession(SessionHolder holder, Session<T> session) {
    }

    default void onRemoveSession(SessionHolder holder, Session<T> session) {
    }

}
