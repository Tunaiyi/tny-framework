package com.tny.game.net.app.listener;

import com.tny.game.net.netty.NettyServer;

@FunctionalInterface
public interface SeverClosedListener {

    void onClosed(NettyServer server);

}
