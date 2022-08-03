package com.tny.game.common.worker;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WorkerCommandBox<C extends Command, CB extends CommandBox<C>> implements CommandBox<C>, CommandBoxWorker {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

    protected volatile CommandBoxWorker worker;

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
        return this.runUseTime;
    }

    public int getProcessSize() {
        return this.runSize;
    }

    @Override
    public boolean isOnCurrentThread() {
        CommandBoxWorker worker = this.worker;
        if (worker != null) {
            worker.isOnCurrentThread();
        }
        return false;
    }

    protected Collection<CB> boxes() {
        Queue<CB> boxes = this.commandBoxList;
        if (boxes != null) {
            return boxes;
        }
        return ImmutableList.of();
    }

    protected Queue<CB> createAndGetBox() {
        if (this.commandBoxList != null) {
            return this.commandBoxList;
        }
        synchronized (this) {
            this.commandBoxList = new ConcurrentLinkedQueue<>();
        }
        return this.commandBoxList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean register(CommandBox<?> commandBox) {
        if (commandBox.bindWorker(this)) {
            createAndGetBox().add((CB)commandBox);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox<?> commandBox) {
        Collection<? extends CommandBox<?>> boxes = boxes();
        if (boxes.remove(commandBox)) {
            return commandBox.unbindWorker();
        }
        return false;
    }

    @Override
    public boolean isWorking() {
        return this.worker != null && this.worker.isWorking();
    }

}
