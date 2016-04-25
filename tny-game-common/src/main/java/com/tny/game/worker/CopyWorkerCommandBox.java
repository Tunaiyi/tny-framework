package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CopyWorkerCommandBox extends AbstractWorkerCommandBox {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected Queue<Command<?>> fromQueue;

    protected Queue<Command<?>> toQueue;

    protected AtomicBoolean accpetState = new AtomicBoolean(false);

    public CopyWorkerCommandBox(Queue<Command<?>> fromQueue, Queue<Command<?>> toQueue) {
        super(new ConcurrentLinkedQueue<>());
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.queue = fromQueue;
    }

    public CopyWorkerCommandBox() {
        this(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    }

    protected Queue<Command<?>> acceptQueue() {
        synchronized (this) {
            Queue<Command<?>> accQueue = this.queue;
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
        Queue<Command<?>> currentRunQueue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        runSize = 0;
        Command<?> command = currentRunQueue.poll();
        while (command != null) {
            if (command.isDone()) {
                command.execute();
                runSize++;
                if (!command.isCompleted()) {
                    this.queue.add(command);
                }
            }
            command = currentRunQueue.poll();
        }
        for (CommandBox commandBox : commandBoxList) {
            commandBox.run();
            runSize += commandBox.getRunSize();
        }
        long finishTime = System.currentTimeMillis();
        runUseTime = finishTime - startTime;
    }

}
