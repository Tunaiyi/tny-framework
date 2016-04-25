package com.tny.game.net.dispatcher.command;

import com.tny.game.worker.Callback;

public interface UserCommandBox {

    boolean appoint(UserCommand<?> command);

    <T> boolean appoint(UserCommand<T> command, Callback<T> callback);

    boolean isEmpty();

    int size();

    void clear();

    int getRunSize();

    long getRunUseTime();

}
