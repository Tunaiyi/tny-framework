package com.tny.game.suite.app.listener;

import com.tny.game.net.netty.NettyServer;

@FunctionalInterface
public interface SeverClosedListener {

    void onClosed(NettyServer server);

}
