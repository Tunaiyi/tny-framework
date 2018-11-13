package com.tny.game.net.endpoint.listener;

import com.tny.game.net.endpoint.Session;
import com.tny.game.net.transport.Tunnel;

@FunctionalInterface
public interface SessionAcceptListener<UID> extends SessionListener {

    void onAccept(Session<UID> session, Tunnel<UID> tunnel);

}
