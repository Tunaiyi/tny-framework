package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import com.tny.game.worker.command.CommandTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public abstract class AbstractWorkerCommandBox extends WorkerCommandBox {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected volatile Queue<CommandTask<?>> queue;

    public AbstractWorkerCommandBox(Queue<CommandTask<?>> queue) {
        super();
        this.queue = queue;
    }

    protected abstract Queue<CommandTask<?>> acceptQueue();

    protected Queue<CommandTask<?>> getQueue() {
        return queue;
    }

    protected <T> boolean executeIfCurrent0(Command<T> command, Callback<T> callBacks) {
        CommandTask<T> commandTask = new DefaultCommandTask<T, Command<T>>(command, callBacks);
        if (this.worker != null && this.worker.getWorkerThread() == Thread.currentThread() && commandTask.getCommand().isCanExecute()) {
            commandTask.run();
        }
        if (!commandTask.getCommand().isCompleted()) {
            addTask(commandTask);
        }
        return true;
    }

    protected void addTask(CommandTask<?> futureTask) {
        this.queue.add(futureTask);
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
