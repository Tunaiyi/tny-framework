package com.tny.game.net.base.listener;


import com.tny.game.net.base.ServerBootstrap;

@FunctionalInterface
public interface ServerClosedListener {

    void onClosed(ServerBootstrap serverBootstrap);

}
