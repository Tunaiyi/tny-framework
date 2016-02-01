package com.tny.game.net.dispatcher.command;

import com.tny.game.worker.Callback;

public interface UserCommandBox {

    public boolean appoint(UserCommand<?> command);

    public <T> boolean appoint(UserCommand<T> command, Callback<T> callback);

    public boolean addCommand(UserCommand<?> command);

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract void clear();

    public abstract int getRunSize();

    public abstract long getRunUseTime();

}
