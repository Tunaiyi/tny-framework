package com.tny.game.net.base;

import com.tny.game.net.base.listener.ServerClosedListener;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface ServerBootstrap {

    String getName();

    void open();

    boolean isBound();

    boolean close();

    void addClosedListener(ServerClosedListener listener);

    void addClosedListeners(Collection<ServerClosedListener> listenerCollection);

    void clearClosedListener();
}
