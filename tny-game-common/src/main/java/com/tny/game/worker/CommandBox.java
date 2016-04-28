package com.tny.game.worker;

import com.tny.game.worker.command.Command;


public abstract class CommandBox<C extends Command> implements CommandWorker {

    public abstract boolean accept(C command);

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract void clear();

    public abstract int getRunSize();

    public abstract long getRunUseTime();

    protected abstract boolean bindWorker(CommandWorker worker);

    protected abstract boolean unbindWorker();

    protected abstract void run();

}
