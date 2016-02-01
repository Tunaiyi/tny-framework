package com.tny.game.net.dispatcher.command;

import com.tny.game.net.base.Protocol;
import com.tny.game.worker.command.BaseCommand;

public abstract class BaseUserCommand<T> extends BaseCommand<T> implements UserCommand<T> {

    protected long userID;

    protected int protocol;

    public BaseUserCommand(String name, long userID, int protocol) {
        super(name);
        this.userID = userID;
        this.protocol = protocol;
    }

    public BaseUserCommand(String name, long userID, Protocol protocol) {
        super(name);
        this.userID = userID;
        this.protocol = protocol.getProtocol();
    }

    @Override
    public long getUserID() {
        return this.userID;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

}
