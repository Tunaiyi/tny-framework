package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.Command;

public interface MessageCommandBox {

    boolean accept(Command command);

    boolean accept(Runnable runnable);

    int size();

}
