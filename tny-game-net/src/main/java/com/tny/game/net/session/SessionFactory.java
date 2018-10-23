package com.tny.game.net.session;

import com.tny.game.net.transport.Certificate;

public interface SessionFactory<UID> {

    NetSession<UID> createSession(Certificate<UID> certificate);

}
