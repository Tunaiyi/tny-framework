package com.tny.game.net.transport;

public interface SessionFactory<UID> {

    NetSession<UID> createSession(Certificate<UID> certificate);

}
