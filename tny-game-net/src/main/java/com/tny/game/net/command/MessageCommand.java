package com.tny.game.net.command;

import com.tny.game.worker.Callback;
import com.tny.game.worker.command.Command;

public interface MessageCommand<T> extends Command {

    T invoke();

    default String getName() {
        return this.getClass().getName();
    }

    void setCallback(Callback<?> callback);

}