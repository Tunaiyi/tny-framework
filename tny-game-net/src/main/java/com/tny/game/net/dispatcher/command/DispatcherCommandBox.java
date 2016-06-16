package com.tny.game.net.dispatcher.command;

import com.tny.game.net.dispatcher.DispatcherCommand;
import com.tny.game.worker.Callback;

public interface DispatcherCommandBox {

    boolean appoint(DispatcherCommand<?> command);

    <T> boolean appoint(DispatcherCommand<T> command, Callback<T> callback);

    boolean appoint(Runnable runnable);

    boolean isEmpty();

    int size();

    void clear();

    int getRunSize();

    long getRunUseTime();

}
