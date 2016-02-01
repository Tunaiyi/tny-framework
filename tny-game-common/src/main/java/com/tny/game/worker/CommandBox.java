package com.tny.game.worker;

import com.tny.game.worker.command.Command;


public abstract class CommandBox {

    public abstract boolean appoint(Command<?> command);

    public abstract <T> boolean appoint(Command<T> command, Callback<T> callback);

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract void clear();

    public abstract boolean bindWorker(WorldWorker worker);

    public abstract boolean unbindWorker();

    public abstract boolean register(CommandBox commandBox);

    public abstract boolean unregister(CommandBox commandBox);

    public abstract int getRunSize();

    public abstract long getRunUseTime();

    protected abstract void run();


}
