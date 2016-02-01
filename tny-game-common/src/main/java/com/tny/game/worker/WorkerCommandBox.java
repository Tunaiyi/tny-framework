package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import com.tny.game.worker.command.CommandTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WorkerCommandBox extends CommandBox {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected WorldWorker worker;

    protected long runUseTime;

    protected int runSize;

    protected abstract Queue<CommandTask<?>> acceptQueue();

    protected Queue<CommandBox> commandBoxList = new ConcurrentLinkedQueue<CommandBox>();

    protected abstract <T> boolean executeIfCurrent0(Command<T> command, Callback<T> callBack);

    @Override
    public boolean appoint(Command<?> command) {
        return executeIfCurrent0(command, null);
    }

    @Override
    public <T> boolean appoint(Command<T> command, Callback<T> callBacks) {
        return executeIfCurrent0(command, callBacks);
    }

    public long getRunUseTime() {
        return runUseTime;
    }

    public int getRunSize() {
        return runSize;
    }

    protected WorldWorker getWorldWorker() {
        return worker;
    }

    @Override
    public boolean register(CommandBox commandBox) {
        if (commandBox.bindWorker(this.worker)) {
            CommandBox workerCommandBox = (WorkerCommandBox) commandBox;
            this.commandBoxList.add(workerCommandBox);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (commandBox.unbindWorker()) {
            return this.commandBoxList.remove(commandBox);
        }
        return false;
    }

    public abstract void run();

}
