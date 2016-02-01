package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.CommandTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoppyWorkerCommandBox extends AbstractWorkerCommandBox {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected Queue<CommandTask<?>> fromQueue;

    protected Queue<CommandTask<?>> toQueue;

    protected AtomicBoolean accpetState = new AtomicBoolean(false);

    public CoppyWorkerCommandBox(Queue<CommandTask<?>> fromQueue, Queue<CommandTask<?>> toQueue) {
        super(new ConcurrentLinkedQueue<CommandTask<?>>());
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.queue = fromQueue;
    }

    public CoppyWorkerCommandBox() {
        this(new ConcurrentLinkedQueue<CommandTask<?>>(), new ConcurrentLinkedQueue<CommandTask<?>>());
    }

    protected Queue<CommandTask<?>> acceptQueue() {
        synchronized (this) {
            Queue<CommandTask<?>> accQueue = this.queue;
            this.queue = accQueue != this.toQueue ? this.toQueue : this.fromQueue;
            this.queue = accQueue;
            return accQueue;
        }
    }

    public void clear() {
        queue.clear();
        toQueue.clear();
        fromQueue.clear();
    }

    public boolean isEmpty() {
        return toQueue.isEmpty() && fromQueue.isEmpty();
    }

    public int size() {
        return toQueue.size() + fromQueue.size();
    }

    public void run() {
        Queue<CommandTask<?>> currentRunQueue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        runSize = 0;
        CommandTask<?> task = currentRunQueue.poll();
        while (task != null) {
            if (task.getCommand().isWorking()) {
                if (task.getCommand().isCanExecute()) {
                    task.run();
                    runSize++;
                    if (!task.getCommand().isCompleted()) {
                        this.queue.add(task);
                    }
                } else {
                    this.queue.add(task);
                }
            } else {
                task.fail(false);
            }
            task = currentRunQueue.poll();
        }
        for (CommandBox commandBox : commandBoxList) {
            commandBox.run();
            runSize += commandBox.getRunSize();
        }
        long finishTime = System.currentTimeMillis();
        runUseTime = finishTime - startTime;
    }

}
