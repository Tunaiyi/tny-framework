package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Session;

@FunctionalInterface
public interface SessionCloseListener<UID> extends SessionListener {

    void onClose(Session<UID> session);

}
