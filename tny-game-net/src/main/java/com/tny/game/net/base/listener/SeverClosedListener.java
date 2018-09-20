package com.tny.game.net.base.listener;

import com.tny.game.net.netty.NettyBinder;

@FunctionalInterface
public interface SeverClosedListener {

    void onClosed(NettyBinder server);

}
