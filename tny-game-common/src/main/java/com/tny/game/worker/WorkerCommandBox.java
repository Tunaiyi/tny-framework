package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WorkerCommandBox extends CommandBox {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected WorldWorker worker;

    protected long runUseTime;

    protected int runSize;

    protected volatile Queue<CommandBox> commandBoxList;

    protected abstract Queue<Command> acceptQueue();

    protected abstract <T> boolean executeIfCurrent0(Command command);

    @Override
    public boolean appoint(Command command) {
        return executeIfCurrent0(command);
    }

//    @Override
//    public <T> boolean appoint(Command<T> command, Callback<T> callBacks) {
//        return executeIfCurrent0(command, callBacks);
//    }

    public long getRunUseTime() {
        return runUseTime;
    }

    public int getRunSize() {
        return runSize;
    }

    protected WorldWorker getWorldWorker() {
        return worker;
    }

    private Queue<CommandBox> boxes() {
        if (commandBoxList != null)
            return commandBoxList;
        synchronized (this) {
            this.commandBoxList = new ConcurrentLinkedQueue<>();
        }
        return this.commandBoxList;
    }

    @Override
    public boolean register(CommandBox commandBox) {
        if (commandBox.bindWorker(this.worker)) {
            boxes().add(commandBox);
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

    public abstract void run();

}
