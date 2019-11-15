package com.tny.game.common.worker;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WorkerCommandBox<C extends Command, CB extends CommandBox> implements CommandBox<C> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Logs.WORKER);

    protected volatile CommandWorker worker;

    protected long runUseTime;

    protected int runSize;

    private volatile Queue<CB> commandBoxList;

    protected abstract Queue<C> acceptQueue();

    protected abstract boolean executeIfCurrent(C command);

    @Override
    public boolean accept(C command) {
        return executeIfCurrent(command);
    }

    public long getProcessUseTime() {
        return runUseTime;
    }

    public int getProcessSize() {
        return runSize;
    }

    @Override
    public boolean isOnCurrentThread() {
        CommandWorker worker = this.worker;
        if (worker != null)
            worker.isOnCurrentThread();
        return false;
    }

    protected Collection<CB> boxes() {
        Queue<CB> boxes = commandBoxList;
        if (boxes != null)
            return boxes;
        return ImmutableList.of();
    }

    protected Queue<CB> createAndGetBox() {
        if (commandBoxList != null)
            return commandBoxList;
        synchronized (this) {
            this.commandBoxList = new ConcurrentLinkedQueue<>();
        }
        return this.commandBoxList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean register(CommandBox commandBox) {
        if (commandBox.bindWorker(this)) {
            createAndGetBox().add((CB) commandBox);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (boxes().remove(commandBox)) {
            return commandBox.unbindWorker();
        }
        return false;
    }

    @Override
    public boolean isWorking() {
        return this.worker != null && this.worker.isWorking();
    }


}
