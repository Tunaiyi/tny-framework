package com.tny.game.net.session;

import com.tny.game.net.tunnel.NetTunnel;

public interface SessionFactory<UID> {

    NetSession<UID> createSession(NetTunnel<UID> tunnel);

}
