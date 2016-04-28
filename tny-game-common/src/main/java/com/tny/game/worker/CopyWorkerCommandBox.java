package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CopyWorkerCommandBox<C extends Command, CB extends CommandBox>  extends AbstractWorkerCommandBox<C, CB> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected Queue<C> fromQueue;

    protected Queue<C> toQueue;

    private Lock lock = new ReentrantLock();

    public CopyWorkerCommandBox(Queue<C> fromQueue, Queue<C> toQueue) {
        super(new ConcurrentLinkedQueue<>());
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.queue = fromQueue;
    }

    public CopyWorkerCommandBox() {
        this(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    }

    protected Queue<C> acceptQueue() {
        while (true) {
            if (lock.tryLock()) {
                Queue<C> accQueue = this.queue;
                this.queue = accQueue != this.toQueue ? this.toQueue : this.fromQueue;
                this.queue = accQueue;
                return accQueue;
            } else {
                Thread.yield();
            }
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
        Queue<C> currentRunQueue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        runSize = 0;
        C cmd = currentRunQueue.poll();
        while (cmd != null) {
            if (cmd.isWork()) {
                executeCommand(cmd);
                runSize++;
                if (!cmd.isDone()) {
                    this.queue.add(cmd);
                }
            }
            cmd = currentRunQueue.poll();
        }
        for (CommandBox commandBox : commandBoxList) {
            this.worker.run(commandBox);
            runSize += commandBox.getRunSize();
        }
        long finishTime = System.currentTimeMillis();
        runUseTime = finishTime - startTime;
    }

}
