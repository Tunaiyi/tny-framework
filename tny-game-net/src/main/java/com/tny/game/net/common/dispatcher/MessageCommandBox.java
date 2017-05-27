package com.tny.game.net.common.dispatcher;

import com.tny.game.worker.command.Command;

public interface MessageCommandBox {

    boolean appoint(Command command);

    boolean appoint(Runnable runnable);

    int size();

}
