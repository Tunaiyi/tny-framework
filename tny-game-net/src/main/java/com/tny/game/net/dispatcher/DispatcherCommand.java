package com.tny.game.net.dispatcher;

import com.tny.game.worker.Callback;
import com.tny.game.worker.command.Command;

public interface DispatcherCommand<T> extends Command {

    T invoke();

    long getUserID();

    default String getName() {
        return this.getClass().getName();
    }

    void setCallback(Callback<?> callback);

}