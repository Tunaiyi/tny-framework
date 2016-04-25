package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public abstract class AbstractWorkerCommandBox extends WorkerCommandBox {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected volatile Queue<Command<?>> queue;

    public AbstractWorkerCommandBox(Queue<Command<?>> queue) {
        super();
        this.queue = queue;
    }

    protected abstract Queue<Command<?>> acceptQueue();

    protected Queue<Command<?>> getQueue() {
        return queue;
    }

    protected <T> boolean executeIfCurrent0(Command<T> command, Callback<T> callBacks) {
        if (callBacks != null)
            command = new ProxyCommand<>(command, callBacks);
        if (this.worker != null && this.worker.getWorkerThread() == Thread.currentThread()) {
            command.execute();
        }
        if (!command.isCompleted()) {
            addCommand(command);
        }
        return true;
    }

    protected void addCommand(Command<?> command) {
        this.queue.add(command);
    }

    @Override
    public synchronized boolean bindWorker(WorldWorker worker) {
        if (this.worker != null)
            return false;
        this.worker = worker;
        for (CommandBox box : commandBoxList)
            box.bindWorker(worker);
        return true;
    }

    @Override
    public synchronized boolean unbindWorker() {
        if (this.worker == null)
            return false;
        this.worker = null;
        for (CommandBox box : commandBoxList)
            box.unbindWorker();
        return true;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

}
