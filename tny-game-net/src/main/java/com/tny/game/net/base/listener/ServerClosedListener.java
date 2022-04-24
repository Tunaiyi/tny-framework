package com.tny.game.net.base.listener;

import com.tny.game.net.base.*;

@FunctionalInterface
public interface ServerClosedListener {

    void onClosed(ServerGuide serverBootstrap);

}
