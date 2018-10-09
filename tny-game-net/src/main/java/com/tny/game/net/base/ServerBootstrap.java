package com.tny.game.net.base;

import com.tny.game.net.base.listener.SeverClosedListener;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface ServerBootstrap {

    String getName();

    void open();

    boolean isBound();

    boolean close();

    void addClosedListener(SeverClosedListener listener);

    void addClosedListeners(Collection<SeverClosedListener> listenerCollection);

    void clearClosedListener();
}
