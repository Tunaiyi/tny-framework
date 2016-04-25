package com.tny.game.net.dispatcher.command;

import com.tny.game.worker.command.Command;

public interface UserCommand<T> extends Command<T> {

    long getUserID();

    int getProtocol();

}
