package com.tny.game.net.common.dispatcher;

import com.tny.game.net.command.DispatchCommand;
import com.tny.game.worker.Callback;

public interface MessageCommandBox {

    boolean appoint(DispatchCommand<?> command);

    <T> boolean appoint(DispatchCommand<T> command, Callback<T> callback);

    boolean appoint(Runnable runnable);

    int size();

}
