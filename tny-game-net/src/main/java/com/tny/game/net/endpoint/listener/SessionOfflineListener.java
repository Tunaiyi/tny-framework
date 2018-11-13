package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Session;

@FunctionalInterface
public interface SessionOfflineListener<UID> extends SessionListener {

    void onOffline(Session<UID> session);

}
