package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Session;

@FunctionalInterface
public interface SessionOnlineListener<UID> extends SessionListener {

    void onOnline(Session<UID> session);

}
