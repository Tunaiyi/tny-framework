package com.tny.game.net.dispatcher;

import com.tny.game.net.command.MessageCommand;
import com.tny.game.worker.Callback;

public interface MessageCommandBox {

    boolean appoint(MessageCommand<?> command);

    <T> boolean appoint(MessageCommand<T> command, Callback<T> callback);

    boolean appoint(Runnable runnable);

    int size();

}
