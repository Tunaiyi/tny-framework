package com.tny.game.suite.app;

import com.tny.game.suite.app.listener.SeverClosedListener;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface Server {

    String getName();

    void open();

    boolean isBound();

    boolean close();

    void addClosedListener(SeverClosedListener listener);

    void addClosedListeners(Collection<SeverClosedListener> listenerCollection);

    void clearClosedListener();
}
