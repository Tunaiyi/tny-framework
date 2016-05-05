package com.tny.game.worker;

import com.tny.game.worker.command.Command;


public interface CommandBox<C extends Command> extends CommandWorker {

    boolean isEmpty();

    int size();

    void clear();

    int getProcessSize();

    long getProcessUseTime();

    boolean accept(C command);

    boolean bindWorker(CommandWorker worker);

    boolean unbindWorker();

    void process();

}
