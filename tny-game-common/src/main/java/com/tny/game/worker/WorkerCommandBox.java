package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WorkerCommandBox<C extends Command, CB extends CommandBox> extends CommandBox<C> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected volatile CommandWorker worker;

    protected long runUseTime;

    protected int runSize;

    protected volatile Queue<CB> commandBoxList;

    protected abstract Queue<C> acceptQueue();

    protected abstract boolean executeIfCurrent(C command);

    @Override
    public boolean accept(C command) {
        return executeIfCurrent(command);
    }

    public long getRunUseTime() {
        return runUseTime;
    }

    public int getRunSize() {
        return runSize;
    }

    @Override
    public boolean isOnCurrentThread() {
        CommandWorker worker = this.worker;
        if (worker != null)
            worker.isOnCurrentThread();
        return false;
    }

    private Queue<CB> boxes() {
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
            boxes().add((CB) commandBox);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (commandBox.unbindWorker()) {
            return boxes().remove(commandBox);
        }
        return false;
    }

}
